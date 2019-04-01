package pizzapipeline.server.device;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.action.CookInOvenAction;
import pizzapipeline.server.item.Item;

public class OvenDevice extends Device {
    private static final Logger log = LoggerFactory.getLogger(OvenDevice.class);

    public OvenDevice(@NotNull String name) {
        super(name);
    }

    @Override
    protected InterractionResult interact(Item item, Action action) {

        InterractionResult result = InterractionResult.FAILED;
        switch (item.getType()) {
            case PIZZA:
                int secondsToCook = ((CookInOvenAction)action).getSecondsRequired();
                log.debug("Oven start cooking pizza - it will take {} seconds", secondsToCook);
                result = cook(TimeUnit.SECONDS.toMillis(secondsToCook));
                log.debug("Pizza cooked with result {}", result);
                break;
            default:
        }

        return result;
    }

    private static InterractionResult cook(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Cooking interrupted due to exception", e);
            return InterractionResult.FAILED;
        }
        return InterractionResult.SUCCESS;
    }

    public boolean putInItem(long itemId, @NotNull ActionType actionType) {
        Validate.notNull(actionType);

        try {
            lock.readLock().tryLock(10, TimeUnit.MILLISECONDS);

            if (actionType == ActionType.MOVE_TO_OVEN && deviceState != DeviceState.FREE_WITH_ITEM) {
                throw new IllegalStateException("Unable to move to oven if have nothing");
            } else if (deviceState != DeviceState.FREE &&
                    !(deviceState == DeviceState.FREE_WITH_ITEM && itemOnTable == itemId)) {
                log.debug("Fail to apply action {} due to device state {}", actionType, deviceState);
                return false;
            }

        } catch (InterruptedException e) {
            log.debug("Fail to apply action due to tryLock.readLock timeout", e);
            return false;
        } finally {
            lock.readLock().unlock();
        }

        boolean success = setDeviceState(DeviceState.FREE_WITH_ITEM);

        if (success) {
            itemOnTable = itemId;
        }
        return success;
    }

    @Override
    public String toString() {
        return "OvenDevice{}";
    }
}
