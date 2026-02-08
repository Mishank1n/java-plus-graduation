package ru.practicum.starter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.interaction.UserInteractionService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInteractionReaderStarter implements Runnable {

    Properties userInteractionConsumerProperties;
    UserInteractionService service;

    @Override
    public void run() {
        try (Consumer<Long, UserActionAvro> consumer = new KafkaConsumer<>(userInteractionConsumerProperties)) {
            consumer.subscribe(Collections.singletonList("stats.user-actions.v1"));
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(100));
                for (var record : records) {
                    UserActionAvro event = record.value();

                    if (event == null) continue;
                    log.info("Прочитано {}", event.toString());
                    service.collect(event);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("UserInteractionReader остановлен");
        } catch (Exception e) {
            log.error("Ошибка в цикле агрегации", e);
        } finally {
            log.info("UserInteractionReader остановлен");
        }
    }
}

