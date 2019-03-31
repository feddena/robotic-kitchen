package pizzapipeline.server;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    public Application() {
    }

    @PreDestroy
    public void onDestroy() {
        LOG.info("APPLICATION SHUTDOWN GRACEFULLY");
    }

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        LOG.info("READY TO COOK SOME PIZZA");
    }
}

