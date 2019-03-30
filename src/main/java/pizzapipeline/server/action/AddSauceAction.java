package pizzapipeline.server.action;

public class AddSauceAction extends Action {

    public AddSauceAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public ActionType getType() {
        return ActionType.ADD_SAUCE;
    }
}
