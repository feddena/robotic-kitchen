package pizzapipeline.server.action;

import pizzapipeline.server.Location;

public class MoveFromOvenAction extends MoveAction {
    public MoveFromOvenAction(int ordinalNumber) {
        super(ordinalNumber, Location.OVEN, Location.TABLE_AFTER_OVEN);
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE_FROM_OVEN;
    }
}
