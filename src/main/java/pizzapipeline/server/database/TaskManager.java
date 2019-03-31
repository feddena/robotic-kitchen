package pizzapipeline.server.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
    private final static String ITEM_TASKS_QUEUE = "queue:item-tasks";
    private final static String ACTION_TASKS_QUEUE = "queue:action-tasks-";

    private final static String PIZZA_TASK = "PIZZA";

    private final RedisClient redisClient;

    @Autowired
    public TaskManager(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void addActionTask(long itemId, int actionOrdinalNumber) {
        addTask(String.valueOf(actionOrdinalNumber), ACTION_TASKS_QUEUE + itemId);
    }

    @Nullable
    public Integer getNextActionOrderId(long itemId) {
        String actionId = getTask(ACTION_TASKS_QUEUE + itemId);
        return actionId == null ? null : Integer.valueOf(actionId);
    }


    public void addTaskPizza() {
        addTask(PIZZA_TASK, ITEM_TASKS_QUEUE);
    }

    public boolean needToMakePizza() {
        String task = getTask(ITEM_TASKS_QUEUE);
        if (task != null && !task.equals(PIZZA_TASK)) {
            throw new IllegalStateException("It's impossible to process task " + task);
        }
        return task != null;
    }

    private void addTask(String task, String queue) {
        redisClient.lpush(queue, task);
    }

    private String getTask(String queue) {
        return redisClient.rpop(queue);
    }
}
