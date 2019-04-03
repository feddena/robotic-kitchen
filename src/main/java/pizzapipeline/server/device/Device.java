package pizzapipeline.server.device;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.ItemState;
import pizzapipeline.server.kitchen.Kitchen;

public abstract class Device {
    private final static Logger log = LoggerFactory.getLogger(Device.class);
    protected static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final String name;

    private volatile boolean acting;
    private volatile Kitchen.OnSuccessActionJob onSuccessActionJob;

    protected volatile DeviceState deviceState = DeviceState.FREE;
    protected volatile Long itemOnTable;


    public Device(@NotNull String name) {
        Validate.notNull(name);

        this.name = name;
    }

    public Kitchen.OnSuccessActionJob getOnSuccessActionJob() {
        return onSuccessActionJob;
    }

    public void setOnSuccessActionJob(Kitchen.OnSuccessActionJob onSuccessActionJob) {
        this.onSuccessActionJob = onSuccessActionJob;
    }

    public boolean isActing() {
        return acting;
    }

    public void setActing(boolean acting) {
        this.acting = acting;
    }

    abstract InterractionResult interact(Item item, Action action);

    @NotNull
    public String getName() {
        return name;
    }

    /**
     * use if device was locked to perform action but it will not be used to perform it
     */
    public void unlock() {
        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            if (deviceState == DeviceState.BUSY) {
                deviceState = itemOnTable == null ? DeviceState.FREE : DeviceState.FREE_WITH_ITEM;
            }
        } catch (InterruptedException e) {
            log.error("Fail to unlock device due to tryLock.writeLock timeout", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Nullable
    public Long getItemOnTable() {
        return itemOnTable;
    }

    public void pullOut(long itemId) {
        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);

            if (itemOnTable != itemId) {
                return;
            }

            if (deviceState != DeviceState.FREE_WITH_ITEM) {
                throw new IllegalStateException("Unable to pull out from oven " + getName() + " in state=" + deviceState);
            }
            deviceState = DeviceState.FREE;
            itemOnTable = null;

        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean lockToApply(long itemId, ActionType actionType) {
        try {
            lock.readLock().tryLock(10, TimeUnit.MILLISECONDS);

            boolean readyToMoveToOven = actionType == ActionType.MOVE_TO_OVEN && deviceState == DeviceState.FREE_WITH_ITEM;
            boolean readyForNewItem = actionType != ActionType.MOVE_TO_OVEN && deviceState == DeviceState.FREE;
            boolean readyToApplyActionToItemOnTable = deviceState == DeviceState.FREE_WITH_ITEM && itemOnTable == itemId;

            if (!readyToMoveToOven && !readyForNewItem && ! readyToApplyActionToItemOnTable) {
                log.debug("{} fail to apply action {} due to device state {}", getName(), actionType, deviceState);
                return false;
            }

        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
            return false;
        } finally {
            lock.readLock().unlock();
        }

        return setDeviceState(DeviceState.BUSY);
    }

    public void apply(Item item, Action action) {
        InterractionResult result = interact(item, action);
        item.setItemState(new ItemState(action.getActionOrdinalNumber(), result, item.getItemState().getLocation()));

        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            if (action.getType() == ActionType.MOVE_TO_OVEN ) {
                deviceState = DeviceState.FREE;
                itemOnTable = null;
            } else {
                deviceState = DeviceState.FREE_WITH_ITEM;
                itemOnTable = item.getId();
            }
        } catch (InterruptedException e) {
            log.error("Fail to unlock device after applying due to tryLock.writeLock timeout", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected boolean setDeviceState(DeviceState state) {
        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            deviceState = state;
        } catch (InterruptedException e) {
            log.debug("Fail to lock for action due to tryLock.writeLock timeout", e);
            return false;
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(name, device.name) &&
                deviceState == device.deviceState &&
                Objects.equals(itemOnTable, device.itemOnTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, deviceState, itemOnTable);
    }

    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", deviceState=" + deviceState +
                ", itemOnTable=" + itemOnTable +
                '}';
    }
}
