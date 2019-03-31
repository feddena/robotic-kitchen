package pizzapipeline.server.device;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.ItemState;

public abstract class Device {
    private final static Logger log = LoggerFactory.getLogger(Device.class);
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private volatile DeviceState deviceState = DeviceState.FREE;

    public boolean lockToApply() {
        try {
            lock.readLock().tryLock(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
            return false;
        } finally {
            lock.readLock().unlock();
        }

        if (deviceState != DeviceState.FREE) {
            log.debug("Fail to apply action due to device state {}", deviceState);
            return false;
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
            deviceState = DeviceState.FREE;
        } catch (InterruptedException e) {
            log.error("Fail to unlock device after applying due to tryLock.writeLock timeout", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    abstract InterractionResult interact(Item item, Action action);
}
