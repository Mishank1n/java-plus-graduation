package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.starter.EventSimilarityReaderStarter;
import ru.practicum.starter.UserInteractionReaderStarter;

@SpringBootApplication
@EnableDiscoveryClient
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(AnalyzerApplication.class, args);


        UserInteractionReaderStarter userInteractionReaderStarter = context.getBean(
                UserInteractionReaderStarter.class);
        EventSimilarityReaderStarter eventSimilarityReaderStarter =
                context.getBean(EventSimilarityReaderStarter.class);

        // запускаем в отдельном потоке обработчик событий
        // от пользовательских хабов
        Thread userInteractionReader = new Thread(userInteractionReaderStarter);
        userInteractionReader.setName("UserInteractionReaderThread");
        userInteractionReader.start();

        // В текущем потоке начинаем обработку
        // снимков состояния датчиков
        Thread eventSimilarityReader = new Thread(eventSimilarityReaderStarter);
        eventSimilarityReader.setName("EventSimilarityReaderThread");
        eventSimilarityReader.start();
    }
}