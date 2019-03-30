package pizzapipeline.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.action.AddCheeseAction;
import pizzapipeline.server.action.AddSauceAction;
import pizzapipeline.server.action.CookInOvenAction;
import pizzapipeline.server.action.MoveAction;
import pizzapipeline.server.action.MoveFromOvenAction;
import pizzapipeline.server.action.MoveToOvenAction;
import pizzapipeline.server.action.RollOutDoughAction;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.device.OvenDevice;
import pizzapipeline.server.device.RobotDevice;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.PizzaItem;
import pizzapipeline.server.recipe.Recipe;

public class Main {

    public static void main(String[] args) {
        List<Action> actions = new ArrayList<>();
        actions.add(new RollOutDoughAction(0));
        actions.add(new AddSauceAction(1));
        actions.add(new AddCheeseAction(2));
        actions.add(new MoveToOvenAction(3));
        actions.add(new CookInOvenAction(4, 5, 100));
        actions.add(new MoveFromOvenAction(5));


        Recipe pizzaRecipe = new Recipe(actions);
        Item pizza = new PizzaItem(pizzaRecipe);

        Map<ActionType, Device> kitchenTools = new HashMap<>();

        kitchenTools.put(ActionType.COOK_IN_OVEN, new OvenDevice());

        List<ActionType> availableActions = new ArrayList<>();
        availableActions.add(ActionType.ROLL_OUT_THE_DOUGH);
        availableActions.add(ActionType.ADD_CHEESE);
        availableActions.add(ActionType.ADD_SAUCE);
        availableActions.add(ActionType.MOVE_TO_OVEN);

        RobotDevice robotDevice = new RobotDevice("robot1", availableActions);
        kitchenTools.put(ActionType.ADD_CHEESE, robotDevice);
        kitchenTools.put(ActionType.ADD_SAUCE, robotDevice);
        kitchenTools.put(ActionType.ROLL_OUT_THE_DOUGH, robotDevice);
        kitchenTools.put(ActionType.MOVE_TO_OVEN, robotDevice);

        List<ActionType> availableActions2 = new ArrayList<>();
        availableActions2.add(ActionType.MOVE_FROM_OVEN);
        RobotDevice robotDevice2 = new RobotDevice("robot2", availableActions2);

        kitchenTools.put(ActionType.MOVE_FROM_OVEN, robotDevice2);

        Kitchen kitchen = new Kitchen(kitchenTools);

        kitchen.cook(pizza);
    }
}