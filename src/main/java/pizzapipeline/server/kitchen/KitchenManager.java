package pizzapipeline.server.kitchen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.action.AddCheeseAction;
import pizzapipeline.server.action.AddSauceAction;
import pizzapipeline.server.action.CookInOvenAction;
import pizzapipeline.server.action.MoveFromOvenAction;
import pizzapipeline.server.action.MoveToOvenAction;
import pizzapipeline.server.action.PackPizzaAction;
import pizzapipeline.server.action.RollOutDoughAction;
import pizzapipeline.server.action.SlicePizzaAction;
import pizzapipeline.server.database.DistrebutedCounterProvider;
import pizzapipeline.server.database.TaskManager;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.device.OvenDevice;
import pizzapipeline.server.device.RobotDevice;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.PizzaItem;
import pizzapipeline.server.recipe.Recipe;

@Component
public class KitchenManager {
    private final static Logger log = LoggerFactory.getLogger(KitchenManager.class);

    private final Kitchen kitchen;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private final TaskManager taskManager;
    private final DistrebutedCounterProvider distrebutedCounterProvider;

    @Autowired
    public KitchenManager(TaskManager taskManager, DistrebutedCounterProvider distrebutedCounterProvider) {
        this.taskManager = taskManager;
        this.distrebutedCounterProvider = distrebutedCounterProvider;
        kitchen = createDefaultKitchen();
    }

    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(this::cookPizzaIfNeed, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        kitchen.destroy();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            throw e;
        }
    }

    private void cookPizzaIfNeed() {
        log.debug("Checking for new PIZZA tasks");
        if (taskManager.needToMakePizza()) {
            log.info("Schedule cooking pizza for you");
            boolean cookingResult = scheduleCookPizza();
            log.info("Pizza cooking scheduled success={}", cookingResult);
        } else {
            log.debug("Got no new pizza orderss");
        }
    }

    private synchronized boolean scheduleCookPizza() {
        List<Action> actions = new ArrayList<>();
        actions.add(new RollOutDoughAction(0));
        actions.add(new AddSauceAction(1));
        actions.add(new AddCheeseAction(2));
        actions.add(new MoveToOvenAction(3));
        actions.add(new CookInOvenAction(4, 2, 100));
        actions.add(new MoveFromOvenAction(5));
        actions.add(new SlicePizzaAction(6));
        actions.add(new PackPizzaAction(7));
        Recipe pizzaRecipe = new Recipe(actions);
        Item pizza = new PizzaItem(pizzaRecipe, distrebutedCounterProvider.getNewTaskId());

        return kitchen.scheduleCooking(pizza);
    }

    private Kitchen createDefaultKitchen() {

        Map<ActionType, List<Device>> kitchenTools = new HashMap<>();

        List<Device> ovens = new ArrayList<>();
        ovens.add(new OvenDevice("oven1"));
        ovens.add(new OvenDevice("oven2"));
        kitchenTools.put(ActionType.COOK_IN_OVEN, ovens);

        List<ActionType> availableActions = new ArrayList<>();
        availableActions.add(ActionType.ROLL_OUT_THE_DOUGH);
        availableActions.add(ActionType.ADD_CHEESE);
        availableActions.add(ActionType.ADD_SAUCE);
        availableActions.add(ActionType.MOVE_TO_OVEN);

        RobotDevice robotDevice = new RobotDevice("robot1", availableActions);
        RobotDevice robotDevice2 = new RobotDevice("robot2", availableActions);
        List<Device> beforeOvenRobots = new ArrayList<>();
        beforeOvenRobots.add(robotDevice);
        beforeOvenRobots.add(robotDevice2);
        availableActions.forEach(actionType -> kitchenTools.put(actionType, beforeOvenRobots));

        List<ActionType> availableActions2 = new ArrayList<>();
        availableActions2.add(ActionType.MOVE_FROM_OVEN);
        availableActions2.add(ActionType.SLICE_PIZZA);
        availableActions2.add(ActionType.PACK_INTO_BOX);
        RobotDevice robotDevice3 = new RobotDevice("robot3", availableActions2);
        RobotDevice robotDevice4 = new RobotDevice("robot4", availableActions2);
        List<Device> afterOvenRobots = new ArrayList<>();
        afterOvenRobots.add(robotDevice3);
        afterOvenRobots.add(robotDevice4);
        availableActions2.forEach(actionType -> kitchenTools.put(actionType, afterOvenRobots));

        return new Kitchen(kitchenTools, taskManager);
    }
}
