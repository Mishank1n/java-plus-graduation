package ru.practicum.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaSimilarityProducer {

    private final Producer<String, Object> producer;
    private final String topic = "stats.events-similarity.v1";

    public KafkaSimilarityProducer(Properties producerProperties) {
        this.producer = new KafkaProducer<>(producerProperties);
    }

    public void sendSimilarity(long eventA, long eventB, Object similarityAvro) {
        String key = eventA + "-" + eventB;
        producer.send(new ProducerRecord<>(topic, key, similarityAvro));
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
