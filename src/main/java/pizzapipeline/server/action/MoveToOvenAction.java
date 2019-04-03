package pizzapipeline.server.action;

public class MoveToOvenAction extends MoveAction {
    public MoveToOvenAction(int ordinalNumber) {
        super(ordinalNumber, Location.TABLE_BEFORE_OVEN, Location.OVEN);
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE_TO_OVEN;
    }
}
