package pizzapipeline.server.action;

public class AddCheeseAction extends Action {

    public AddCheeseAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public ActionType getType() {
        return ActionType.ADD_CHEESE;
    }
}
