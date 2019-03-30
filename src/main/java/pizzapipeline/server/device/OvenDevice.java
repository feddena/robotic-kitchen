package pizzapipeline.server.device;

import java.util.concurrent.TimeUnit;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.CookInOvenAction;
import pizzapipeline.server.item.Item;

public class OvenDevice extends Device {

    public InterractionResult cook(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Cooking interrupted due to exception : " + e.getMessage());
            return InterractionResult.FAILED;
        }
        return InterractionResult.SUCCESS;
    }

    @Override
    protected InterractionResult interact(Item item, Action action) {

        InterractionResult result = InterractionResult.FAILED;
        switch (item.getType()) {
            case PIZZA:
                int secondsToCook = ((CookInOvenAction)action).getSecondsRequired();
                System.out.println("Oven start cooking pizza - it will take " + secondsToCook + " seconds");
                result = cook(TimeUnit.SECONDS.toMillis(secondsToCook));
                System.out.println("Pizza cooked with result " + result);
                break;
            default:
        }

        return result;
    }

    @Override
    public String toString() {
        return "OvenDevice{}";
    }
}
