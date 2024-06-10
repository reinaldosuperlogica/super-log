package com.superlogica.logs;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SuperLogEvent {

    private static final String TAG_EVENT_CONSUMED = "EVENT_CONSUMED";
    private static final String TAG_EVENT_PRODUCED = "EVENT_PRODUCED";

    @Autowired
    private SuperLogBrokerPublisher superLogBrokerPublisher;

    public <T extends DomainEvent> void logConsume(Map<SuperLogEventType, String> eventLoggerTypeValue, DomainEventEnvelope<T> message) {
        this.logConsume(
            eventLoggerTypeValue,
            message,
            null
        );
    }
    
    public <T extends DomainEvent> void logConsume(Map<SuperLogEventType, String> eventLoggerTypeValue, DomainEventEnvelope<T> message, Object details) {
        SuperLog.info(            
            SuperLogEventMessage.builder()
                .id(message.getMessage().getId())
                .key(message.getAggregateId())
                .topic(message.getAggregateType())
                .event(message.getEvent().getClass().getName())
                .details(details)
                .build(),
            eventLoggerTypeValueToTag(SuperLogEvent.TAG_EVENT_CONSUMED, eventLoggerTypeValue),
            superLogBrokerPublisher,
            false
        );
    }

    public <T, E> void logProduce(Map<SuperLogEventType, String> eventLoggerTypeValue, Class<T> topic, Class<E> event, String key) {
        this.logProduce(
            eventLoggerTypeValue,
            topic,
            event,
            key,
            null
        );
    }

    public <T, E> void logProduce(Map<SuperLogEventType, String> eventLoggerTypeValue, Class<T> topic, Class<E> event, String key, Object details) {
        SuperLog.info(            
            SuperLogEventMessage.builder()
                .key(key)
                .topic(topic.getName())
                .event(event.getName())
                .details(details)
                .build(),
            eventLoggerTypeValueToTag(SuperLogEvent.TAG_EVENT_PRODUCED, eventLoggerTypeValue),
            superLogBrokerPublisher,
            false
        );
    }

    private List<String> eventLoggerTypeValueToTag(String defaultTag, Map<SuperLogEventType, String> eventLoggerType) {
        List<String> tags = new ArrayList<>();

        tags.add(defaultTag);

        for (Map.Entry<SuperLogEventType, String> entry : eventLoggerType.entrySet()) {
            tags.add(entry.getKey().getValue() + ":" + entry.getValue());
        }

        return tags;
    }
}
