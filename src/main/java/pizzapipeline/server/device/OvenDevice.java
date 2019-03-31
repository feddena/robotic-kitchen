package pizzapipeline.server.device;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.CookInOvenAction;
import pizzapipeline.server.item.Item;

public class OvenDevice extends Device {
    private static final Logger log = LoggerFactory.getLogger(OvenDevice.class);

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

    @Override
    public String toString() {
        return "OvenDevice{}";
    }
}
