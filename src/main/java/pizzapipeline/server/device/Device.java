package pizzapipeline.server.device;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.ItemState;

public abstract class Device {
    private final static Logger log = LoggerFactory.getLogger(Device.class);
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private volatile DeviceState deviceState = DeviceState.FREE;
    private volatile Long itemOnTable;

    public void unlock() {
        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            if (deviceState == DeviceState.BUSY) {
                deviceState = DeviceState.FREE;
            }
        } catch (InterruptedException e) {
            log.error("Fail to unlock device due to tryLock.writeLock timeout", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     *  it's unsafe to use it without success lockToPutIn
     */
    public void unsafePutItem(long itemId) {
        itemOnTable = itemId;
    }

    public boolean lockToPutIn(long itemId, ActionType actionType) {
        try {
            lock.readLock().tryLock(10, TimeUnit.MILLISECONDS);

            if (actionType == ActionType.MOVE_TO_OVEN && deviceState != DeviceState.FREE_WITH_ITEM) {
                throw new IllegalStateException("Unable to move to oven if have nothing");
            } else if (deviceState != DeviceState.FREE &&
                    !(deviceState == DeviceState.FREE_WITH_ITEM && itemOnTable == itemId)) {
                log.debug("Fail to apply action due to device state {}", deviceState);
                return false;
            }

        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
            return false;
        } finally {
            lock.readLock().unlock();
        }

        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            deviceState = DeviceState.FREE_WITH_ITEM;
        } catch (InterruptedException e) {
            log.debug("Fail to lock for action due to tryLock.writeLock timeout", e);
            return false;
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public boolean lockToApply(long itemId, ActionType actionType) {
        try {
            lock.readLock().tryLock(10, TimeUnit.MILLISECONDS);

            if (actionType == ActionType.MOVE_TO_OVEN && deviceState != DeviceState.FREE_WITH_ITEM) {
                throw new IllegalStateException("Unable to move to oven if have nothing");
            } else if (deviceState != DeviceState.FREE &&
                    !(deviceState == DeviceState.FREE_WITH_ITEM && itemOnTable == itemId)) {
                log.debug("Fail to apply action due to device state {}", deviceState);
                return false;
            }

        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
            return false;
        } finally {
            lock.readLock().unlock();
        }

        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            deviceState = DeviceState.BUSY;
        } catch (InterruptedException e) {
            log.debug("Fail to lock for action due to tryLock.writeLock timeout", e);
            return false;
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public void apply(Item item, Action action) {
        InterractionResult result = interact(item, action);
        item.setItemState(new ItemState(action.getActionOrdinalNumber(), result, item.getItemState().getLocation()));

        try {
            lock.writeLock().tryLock(1000, TimeUnit.MILLISECONDS);
            if (action.getType() == ActionType.MOVE_TO_OVEN ) {
                deviceState = DeviceState.FREE;
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

    abstract InterractionResult interact(Item item, Action action);
}
