package pl.mimuw.zpp.quantumai.utmpython;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
@ServletComponentScan
public class UtmPythonApplication {
    private static ConfigurableApplicationContext CONTEXT;

    public static void main(String[] args) {
        CONTEXT = SpringApplication.run(UtmPythonApplication.class, args);
    }

    public static void shutdown() {
        CONTEXT.close();
    }

}
