package pizzapipeline.server.device;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.item.Item;

public class RobotDevice extends Device {
    private static final Logger log = LoggerFactory.getLogger(RobotDevice.class);

    private final List<ActionType> availableActions;

    public RobotDevice(@NotNull String name, @NotNull List<ActionType> availableActions) {
        super(name);
        Validate.notNull(name);
        Validate.notEmpty(availableActions);


        this.availableActions = availableActions;
    }

    @Override
    protected InterractionResult interact(@NotNull Item item, @NotNull Action action) {
        Validate.notNull(item);
        Validate.notNull(action);

        InterractionResult interractionResult = InterractionResult.FAILED;
        if (!availableActions.contains(action.getType())) {
            log.error("Robot {} unable to do {} due to npo ability to perform {}", getName(), action, availableActions);
            return interractionResult;
        }
        switch (action.getType()) {
            case ADD_CHEESE:
                log.debug("{} adding some cheese", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case ROLL_OUT_THE_DOUGH:
                log.debug("{} rolling dough for you", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(5);
                break;
            case ADD_SAUCE:
                log.debug("{} adding some sauce", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case MOVE_TO_OVEN:
                log.debug("{} move pizza to oven", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case MOVE_FROM_OVEN:
                log.debug("{} move pizza from oven", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case SLICE_PIZZA:
                log.debug("{} slice pizza", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            case PACK_INTO_BOX:
                log.debug("{} pack pizza", getName());
                interractionResult = InterractionResult.SUCCESS;
                sleepWell(2);
                break;
            default:
                throw new IllegalArgumentException(getName() + " robot got unexpected action " + action + " it's unable to do it");
        }

        return interractionResult;
    }

    private void sleepWell(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Robot was interrupted due to exception", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RobotDevice that = (RobotDevice) o;
        return Objects.equals(availableActions, that.availableActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), availableActions);
    }

    @Override
    public String toString() {
        return "RobotDevice{" +
                "availableActions=" + availableActions +
                '}';
    }
}
