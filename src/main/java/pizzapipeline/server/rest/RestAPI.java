package pizzapipeline.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pizzapipeline.server.database.TaskManager;

@RestController
public class RestAPI {
    private final static Logger log = LoggerFactory.getLogger(RestAPI.class);

    private final TaskManager taskManager;

    @Autowired
    public RestAPI(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @RequestMapping("/makePizza")
    public String makePizza(@RequestParam(value="amount", defaultValue="1") Integer amount) {
        for (int i = 0; i < amount; i ++) {
            taskManager.addTaskPizza();
        }
        String answer = String.format("Start cooking your %d pizza", amount);
        log.info(answer);
        return answer;
    }
}