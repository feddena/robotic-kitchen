package pizzapipeline.server.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistrebutedCounterProvider {
    private static final String TASK_ID_COUNTER = "task_id";

    private final RedisClient redisClient;

    @Autowired
    public DistrebutedCounterProvider(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public Long getNewTaskId() {
        return redisClient.incr(TASK_ID_COUNTER);
    }
}
