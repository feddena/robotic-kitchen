package pizzapipeline.server.device;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;

import com.sun.istack.internal.NotNull;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.item.Item;

public class RobotDevice extends Device {

    private final String name;
    private final List<ActionType> availableActions;

    public RobotDevice(@NotNull String name, @NotNull List<ActionType> availableActions) {
        Validate.notNull(name);
        Validate.notEmpty(availableActions);

        this.name = name;
        this.availableActions = availableActions;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    protected InterractionResult interact(Item item, Action action) {
        InterractionResult interractionResult = InterractionResult.FAILED;
        if (!availableActions.contains(action.getType())) {
            System.out.println(name + " unable to do " + action + " it's not in my list " + availableActions);
            return interractionResult;
        }
        switch (action.getType()) {
            case ADD_CHEESE:
                System.out.println(name + " adding some cheese");
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case ROLL_OUT_THE_DOUGH:
                System.out.println(name + " rolling dough for you");
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(5);
                break;
            case ADD_SAUCE:
                System.out.println(name + " adding some sauce");
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case MOVE_TO_OVEN:
                System.out.println(name + " move pizza to oven");
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case MOVE_FROM_OVEN:
                System.out.println(name + " move pizza from oven");
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            default:
                throw new IllegalArgumentException(name + " robot got unexpected action " + action + " it's unable to do it");
        }

        return interractionResult;
    }

    private void sleepWell(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Robot was interrupted due to exception : " + e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotDevice that = (RobotDevice) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(availableActions, that.availableActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, availableActions);
    }

    @Override
    public String toString() {
        return "RobotDevice{" +
                "name='" + name + '\'' +
                ", availableActions=" + availableActions +
                '}';
    }
}
