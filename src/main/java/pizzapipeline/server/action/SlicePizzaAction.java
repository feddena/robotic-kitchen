package pizzapipeline.server.action;

import javax.validation.constraints.NotNull;

public class SlicePizzaAction extends Action {
    public SlicePizzaAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.SLICE_PIZZA;
    }

    @Override
    public String toString() {
        return "SlicePizzaAction{}";
    }
}
