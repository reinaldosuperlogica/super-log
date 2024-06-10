package com.superlogica.logs;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SuperLogBrokerConsumer {

    private final String topic;
    private final Consumer<String, String> kafkaConsumer;

    @Autowired
    public SuperLogBrokerConsumer(
            @Value("${superlog.kafka.topic}") String topic,
            @Value("${kafka.bootstrap.servers}") String bootstrapServers,
            @Value("${superlog.kafka.consumer.group-id}") String groupId) {

        this.topic = topic;

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.kafkaConsumer = new KafkaConsumer<>(properties);
    }

    public ConsumerRecords<String, String> consumeSuperLogs(int waitPollDurationSeconds) {
        int varPreviousSeconds = waitPollDurationSeconds > 0 ? waitPollDurationSeconds : 1000;
        kafkaConsumer.subscribe(Arrays.asList(topic));
        return kafkaConsumer.poll(Duration.ofSeconds(varPreviousSeconds));
    }

    public List<String> getConsumeRecordValues(int waitPollDurationSeconds) {
        ConsumerRecords<String, String> records = consumeSuperLogs(waitPollDurationSeconds);

        if (records.isEmpty()){return new ArrayList<>(); }

        List<String> recordValuesList = new ArrayList<>();

        for (ConsumerRecord<String, String> record : records) {
            recordValuesList.add(record.value());
        }

        return recordValuesList;
    }

}
