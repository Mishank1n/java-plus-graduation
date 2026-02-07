package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.starter.AggregatorStarter;

@SpringBootApplication
@EnableDiscoveryClient
public class AggregatorApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApplication.class, args);
        AggregatorStarter aggregator = context.getBean(AggregatorStarter.class);
        aggregator.start();
    }
}