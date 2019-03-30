package pizzapipeline.server.action;

public class StartAction extends Action {
    public StartAction(int ordinalNumber) {
        super(ordinalNumber);
    }

    @Override
    public ActionType getType() {
        return ActionType.START;
    }
}
