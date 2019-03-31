package pizzapipeline.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String makePizza() {
        taskManager.addTaskPizza();
        log.info("Your pizza task added to queue");
        return "Your pizza task added to queue";
    }
}