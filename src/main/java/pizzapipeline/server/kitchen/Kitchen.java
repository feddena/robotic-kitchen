package pizzapipeline.server.kitchen;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.database.TaskManager;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.item.Item;

public class Kitchen {
    private static final Logger log = LoggerFactory.getLogger(Kitchen.class);
    private final Map<ActionType, List<Device>> tools;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5);
    private final ConcurrentHashMap<Long, Item> nowCooking = new ConcurrentHashMap<>();

    private final TaskManager taskManager;

    public Kitchen(@NotNull Map<ActionType, List<Device>> tools, @NotNull TaskManager taskManager) {
        Validate.notNull(tools);
        Validate.notNull(taskManager);

        this.tools = Collections.unmodifiableMap(tools);
        this.taskManager = taskManager;
        executor.scheduleAtFixedRate(this::tryCookSomething, 0, 5, TimeUnit.SECONDS);
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
                log.info("Nothing to cook");
                return;
            }

            log.info("Looking for available devices to cook {}", id2Item);

            id2Item.forEach(idAndItem -> {
                long id = idAndItem.getKey();
                int nextAction = taskManager.getNextActionOrderId(id);
                Item item = idAndItem.getValue();
                Action action = item.getRecipe().getActions().get(nextAction);
                //log.info("Trying to perform {} for {}", action, item.getType());
                tools.get(action.getType()).forEach(device -> {
                    boolean lockedToPerform = device.lockToApply();
                    if (lockedToPerform) {
                        log.info("Performing {} for {}", action, item.getType());
                        device.apply(item, action);
                        if (item.getRecipe().getActions().size() - 1 > nextAction) {
                            log.info("Schedule next action {} for {}", item.getRecipe().getActions().get(nextAction + 1), id);
                            taskManager.addActionTask(id, nextAction + 1);
                        } else {
                            cooked(id);
                            log.info("{} cooked", item.getType());
                        }
                    } else {
                        log.info("Performing {} for {} delayed", action, item.getType());
                        taskManager.addActionTask(id, nextAction);
                    }
                });
            });
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
}
