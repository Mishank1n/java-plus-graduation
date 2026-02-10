package ru.practicum.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.KafkaSimilarityProducer;
import ru.practicum.service.AggregatorServiceImpl;

import java.time.Duration;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatorStarter {

    private final Properties consumerProperties;
    private final KafkaSimilarityProducer similarityProducer;
    private final AggregatorServiceImpl similarityService;

    public void start() {
        log.info("Aggregator запущен");
        try (Consumer<Long, UserActionAvro> consumer = new KafkaConsumer<>(consumerProperties)) {
            consumer.subscribe(java.util.Collections.singletonList("stats.user-actions.v1"));

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(100));
                for (var record : records) {
                    UserActionAvro event = record.value();

                    if (event == null) continue;

                    var similarities = similarityService.process(event);

                    log.info("Получено {} сообщений сходства для события {}", similarities.size(), event.getEventId());
                    for (EventSimilarityAvro sim : similarities) {
                        similarityProducer.sendSimilarity(sim.getEventA(), sim.getEventB(), sim);
                        log.info("Отправлено сходство {}-{} = {}", sim.getEventA(), sim.getEventB(), sim.getScore());
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("Aggregator остановлен");
        } catch (Exception e) {
            log.error("Ошибка в цикле агрегации", e);
        } finally {
            similarityProducer.flush();
            log.info("Aggregator остановлен");
        }
    }
}