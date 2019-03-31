package pizzapipeline.server.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
    private final static String TASKS_QUEUE = "queue:tasks";
    private final static String PIZZA_TASK = "PIZZA";

    private final RedisClient redisClient;

    @Autowired
    public TaskManager(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void addTaskPizza() {
        addTask(PIZZA_TASK);
    }

    public boolean needToMakePizza() {
        String task = getTask();
        if (task != null && !task.equals(PIZZA_TASK)) {
            throw new IllegalStateException("It's impossible to process task " + task);
        }
        return task != null;
    }

    private void addTask(String task) {
        redisClient.lpush(TASKS_QUEUE, task);
    }

    private String getTask() {
        return redisClient.rpop(TASKS_QUEUE);
    }
}
