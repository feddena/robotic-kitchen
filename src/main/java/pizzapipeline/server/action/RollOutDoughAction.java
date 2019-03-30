package pizzapipeline.server.action;

public class RollOutDoughAction extends Action {

    public RollOutDoughAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public ActionType getType() {
        return ActionType.ROLL_OUT_THE_DOUGH;
    }
}
