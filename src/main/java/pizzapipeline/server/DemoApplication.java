package pizzapipeline.server;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pizzapipeline.server.database.DistrebutedCounterProvider;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    private final DistrebutedCounterProvider distrebutedCounterProvider;

    @Autowired
    public DemoApplication(DistrebutedCounterProvider distrebutedCounterProvider) {
        this.distrebutedCounterProvider = distrebutedCounterProvider;
    }

    @PreDestroy
    public void onDestroy() {
        LOG.info("APPLICATION SHUTDOWN GRACEFULLY");
    }

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(DemoApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");

        for (int i = 0; i < 10; ++i) {
            LOG.info("New task will have id {}", distrebutedCounterProvider.getNewTaskId());
        }
    }
}

