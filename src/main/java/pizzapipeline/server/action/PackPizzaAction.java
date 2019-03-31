package pizzapipeline.server.action;

import javax.validation.constraints.NotNull;

public class PackPizzaAction extends Action {

    public PackPizzaAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.PACK_INTO_BOX;
    }

    @Override
    public String toString() {
        return "PackPizzaAction{}";
    }
}
