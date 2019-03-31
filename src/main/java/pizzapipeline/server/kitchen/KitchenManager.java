package pizzapipeline.server.kitchen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import pizzapipeline.server.action.RollOutDoughAction;
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
            log.debug("No pizza to cooked");
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
        Recipe pizzaRecipe = new Recipe(actions);
        Item pizza = new PizzaItem(pizzaRecipe, distrebutedCounterProvider.getNewTaskId());

        return kitchen.scheduleCooking(pizza);
    }

    private Kitchen createDefaultKitchen() {

        Map<ActionType, List<Device>> kitchenTools = new HashMap<>();

        kitchenTools.put(ActionType.COOK_IN_OVEN, Collections.singletonList(new OvenDevice()));

        List<ActionType> availableActions = new ArrayList<>();
        availableActions.add(ActionType.ROLL_OUT_THE_DOUGH);
        availableActions.add(ActionType.ADD_CHEESE);
        availableActions.add(ActionType.ADD_SAUCE);
        availableActions.add(ActionType.MOVE_TO_OVEN);

        RobotDevice robotDevice = new RobotDevice("robot1", availableActions);
        availableActions.forEach(actionType -> kitchenTools.put(actionType, Collections.singletonList(robotDevice)));

        List<ActionType> availableActions2 = new ArrayList<>();
        availableActions2.add(ActionType.MOVE_FROM_OVEN);
        RobotDevice robotDevice2 = new RobotDevice("robot2", availableActions2);
        availableActions2.forEach(actionType -> kitchenTools.put(actionType, Collections.singletonList(robotDevice2)));

        return new Kitchen(kitchenTools, taskManager);
    }
}
