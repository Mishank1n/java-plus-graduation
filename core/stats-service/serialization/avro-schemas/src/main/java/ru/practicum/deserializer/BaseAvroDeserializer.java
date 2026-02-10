package ru.practicum.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private DecoderFactory decoderFactory;
    private Schema schema;

    public BaseAvroDeserializer() {
        this.decoderFactory = DecoderFactory.get();

    }

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    protected void setSchema(Schema schema) {
        this.schema = schema;
    }

    protected void setDecoderFactory(DecoderFactory decoderFactory) {
        this.decoderFactory = decoderFactory;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        // Проверяем, что schema инициализирована
        if (this.schema == null) {
            throw new IllegalStateException("Schema has not been set for this deserializer.");
        }
        try {
            DatumReader<T> reader = new SpecificDatumReader<>(this.schema);
            Decoder decoder = decoderFactory.binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации данных для топика [" + topic + "]", e);
        }
    }

    @Override
    public void close() {
        // Нет ресурсов для закрытия
    }
}