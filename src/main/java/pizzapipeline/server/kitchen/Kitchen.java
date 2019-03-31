package pizzapipeline.server.kitchen;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.database.TaskManager;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.device.OvenDevice;
import pizzapipeline.server.item.Item;

public class Kitchen {
    private static final Logger log = LoggerFactory.getLogger(Kitchen.class);
    private final Map<ActionType, List<Device>> tools;
    private final ScheduledExecutorService executor;
    private final ConcurrentHashMap<Long, Item> nowCooking = new ConcurrentHashMap<>();

    private final TaskManager taskManager;

    public Kitchen(@NotNull Map<ActionType, List<Device>> tools, @NotNull TaskManager taskManager) {
        Validate.notNull(tools);
        Validate.notNull(taskManager);

        this.tools = Collections.unmodifiableMap(tools);
        this.taskManager = taskManager;
        int numDevices = tools.values().stream().map(List::size).mapToInt(a -> a).sum();
        executor = new ScheduledThreadPoolExecutor(numDevices);
        executor.scheduleAtFixedRate(this::tryCookSomething, 0, 1, TimeUnit.SECONDS);
    }

    public void destroy() throws InterruptedException {
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            throw e;
        }
    }

    private void tryCookSomething() {
        try {
            Set<Map.Entry<Long, Item>> id2Item = nowCooking.entrySet();

            if (id2Item.isEmpty()) {
                log.trace("Nothing to cook");
                return;
            }

            log.debug("Looking for available devices to cook {}", id2Item);

            for (Map.Entry<Long, Item> idAndItem : id2Item) {
                long itemId = idAndItem.getKey();
                Integer nextAction = taskManager.getNextActionOrderId(itemId);
                Item item = idAndItem.getValue();
                if (nextAction == null) {
                    log.debug("Skipping {}-{} next action due to receiving null actionId from DB", item.getType(), item.getId());
                    continue;
                }

                Action action = item.getRecipe().getActions().get(nextAction);
                log.debug("Trying to perform {} for {}-{}", action, item.getType(), item.getId());

                boolean lockedToPerform = false;
                for (Device device : tools.get(action.getType())) {
                    if (device.getItemOnTable() != null && device.getItemOnTable() != itemId) {
                        continue; // device have different item -> it's unable to perform action on it
                    }
                    lockedToPerform = device.lockToApply(item.getId(), action.getType()); // try to lock device for action
                    if (lockedToPerform) {
                        boolean lockedToPerform2 = true;
                        if (action.getType() == ActionType.MOVE_TO_OVEN) {  // this action requires lock of second device - oven
                            for (Device oven : tools.get(ActionType.COOK_IN_OVEN)) {
                                lockedToPerform2 = ((OvenDevice)oven).putInItem(item.getId(), ActionType.COOK_IN_OVEN);

                                if (lockedToPerform2) {
                                    break;
                                }
                            }
                        }

                        if (!lockedToPerform2) {    // if second device required but it isn't available
                            device.unlock();
                            delayPerforming(action, item, nextAction);
                            continue;
                        }

                        log.info("{} performing {} for {}-{}", device.getName(), action, item.getType(), itemId);
                        device.apply(item, action);

                        if (action.getType() == ActionType.MOVE_FROM_OVEN) {
                            tools.get(ActionType.COOK_IN_OVEN).forEach(oven -> {
                                if (oven.getItemOnTable() != null && oven.getItemOnTable() == itemId) {
                                    oven.pullOut(itemId);
                                }
                            });
                        }

                        if (item.getRecipe().getActions().size() - 1 > nextAction) {
                            log.debug("Schedule next action {} for {}", item.getRecipe().getActions().get(nextAction + 1), itemId);
                            taskManager.addActionTask(itemId, nextAction + 1);
                        } else {
                            cooked(itemId);
                            device.pullOut(itemId); // hope that some human will do it
                            log.info("{}-{} cooked and packed", item.getType(), itemId);
                        }
                        break;
                    }
                }
                if (!lockedToPerform) {
                    delayPerforming(action, item, nextAction);
                }
            }
        } catch (RuntimeException e) {
            log.error("Fail to cook something due to exception", e);
        }
    }

    public boolean scheduleCooking(@NotNull Item item) {
        Validate.notNull(item);

        List<ActionType> kitchenUnableDoActions =
                item.getRecipe()
                .getActions().stream()
                .map(Action::getType)
                .filter(action -> !tools.containsKey(action))
                .collect(Collectors.toList());

        if (!kitchenUnableDoActions.isEmpty()) {
            log.warn("Kitchen unable to scheduleCooking it because tools for {} unavailable", kitchenUnableDoActions);
            return false;
        }

        log.info("Kitchen starting to scheduleCooking your {}", item.getType());

        taskManager.addActionTask(item.getId(), 0);

        startCooking(item);

        return true;
    }

    private void startCooking(Item item) {
        nowCooking.put(item.getId(), item);
    }

    private void cooked(long id) {
        nowCooking.remove(id);
    }

    private void delayPerforming(Action action, Item item, int nextAction) {
        log.debug("Performing {} for {} delayed", action, item.getType());
        taskManager.addActionTask(item.getId(), nextAction);
    }
}
