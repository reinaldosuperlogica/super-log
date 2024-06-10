package com.superlogica.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SuperLogBrokerPublisher {

    private Producer<String, String> producer;

	@Autowired
    public SuperLogBrokerPublisher(Producer<String, String> producer) {
        this.producer = producer;
    }	

	@Value("${superlog.kafka.topic}")
	private String topic;
    
    public <T> void publish(SuperLog<T> superLog) {
		producer.send(new ProducerRecord<>(topic, this.toJson(superLog)));
		producer.flush();
    }

    private String toJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return object.toString();
        }
    }
}