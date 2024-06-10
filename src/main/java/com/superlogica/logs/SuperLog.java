package com.superlogica.logs;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Component
public class SuperLog <T> {
	private static String env;
	private static List<String> sendTo = new ArrayList<>();
    private String environment = "dev";
    private String application = "gruvi-core";
    private String timestamp = null;
    private List<String> tags;
    private T message;
    private SuperLogLevel level;    
    private SuperLogChannel channel = SuperLogChannel.STDOUT;
    @JsonIgnore
    private SuperLogBrokerPublisher superLogBrokerPublisher;

    private static Logger logger = LoggerFactory.getLogger(SuperLog.class);
    
    public SuperLog(SuperLogLevel level, SuperLogMessage<T> message, List<String> tags) {
    	this.environment = SuperLog.env;
        this.message = message.getMessage();
        this.tags = tags;
        this.level = level;
        this.superLogBrokerPublisher = null;
        this.timestamp = Instant.ofEpochSecond(System.currentTimeMillis() / 1000).toString();
    }

    public SuperLog(SuperLogLevel level, SuperLogMessage<T> message, List<String> tags, SuperLogBrokerPublisher superLogBrokerPublisher) {
    	this.environment = SuperLog.env;
        this.message = message.getMessage();
        this.tags = tags;
        this.level = level;
        this.superLogBrokerPublisher = superLogBrokerPublisher;
        this.timestamp = Instant.ofEpochSecond(System.currentTimeMillis() / 1000).toString();
    }

    public static <T> void info(SuperLogMessage<T> message, List<String> tags) {
    	tryLog(new SuperLog<T>(SuperLogLevel.INFO, message, tags), false);
    }
    
    public static <T> void info(SuperLogMessage<T> message, List<String> tags, SuperLogBrokerPublisher superLogBrokerPublisher, boolean avoidSendToStdout) {
    	tryLog(new SuperLog<T>(SuperLogLevel.INFO, message, tags, superLogBrokerPublisher), avoidSendToStdout);
    }

    public static <T> void warning(SuperLogMessage<T> message, List<String> tags) {
        tryLog(new SuperLog<T>(SuperLogLevel.WARNING, message, tags), false);
    }
    
    public static <T> void warning(SuperLogMessage<T> message, List<String> tags, SuperLogBrokerPublisher superLogBrokerPublisher, boolean avoidSendToStdout) {
        tryLog(new SuperLog<T>(SuperLogLevel.WARNING, message, tags, superLogBrokerPublisher), avoidSendToStdout);
    }
    
    public static <T> void critical(SuperLogMessage<T> message, List<String> tags) {
        tryLog(new SuperLog<T>(SuperLogLevel.CRITICAL, message, tags), false);
    }
    
    public static <T> void critical(SuperLogMessage<T> message, List<String> tags, SuperLogBrokerPublisher superLogBrokerPublisher, boolean avoidSendToStdout) {
        tryLog(new SuperLog<T>(SuperLogLevel.CRITICAL, message, tags, superLogBrokerPublisher), avoidSendToStdout);
    }
    
    private static <T> void tryLog(SuperLog<T> superLog, boolean avoidSendToStdout) {
        Thread t = new Thread(new Runnable() {
            private SuperLog<T> log = superLog;
            private boolean avoidStdout = avoidSendToStdout;

            public void run() { 
                try {
                    if (SuperLog.sendTo.contains(SuperLogChannel.STDOUT.getValue()) && !avoidStdout) {
                        log.setChannel(SuperLogChannel.STDOUT);
                        resolveLogLevel(log, SuperLog.sendTo.contains(SuperLogChannel.BROKER.getValue()));
                    }
                    
                    if ((SuperLog.sendTo.contains(SuperLogChannel.BROKER.getValue()) || superLog.message instanceof SuperLogHttpMessage) && !Objects.isNull(log.superLogBrokerPublisher)) {             
                        log.setChannel(SuperLogChannel.BROKER);
                        log.superLogBrokerPublisher.publish(log);
                    }
                } catch (Exception e) {
                    logger.error("{}: {}", e.getClass(), e.getMessage());
                }
            }

            private void resolveLogLevel(SuperLog<T> log, boolean willSendToBroker) {
                String logText = log.toJson();

                if (!willSendToBroker) {
                    System.out.println(logText);
                    return;
                }

                if (log.getLevel().equals(SuperLogLevel.WARNING) ) {
                    logger.warn(logText);
                    return;
                }
             
                if (log.getLevel().equals(SuperLogLevel.CRITICAL) ) {
                    logger.error(logText);
                    return;
                }

                logger.info(logText);
            }
        });
        t.start();
    }

    private String toJson() {
        return Jackson.toJsonString(this);
    }
    
    @Value("${infra.env:dev}")
    public void setEnv(String env) {
    	SuperLog.env = env;
    }
    
    @Value("${superlog.send.to:stdout}")
    public void setSendTo(List<String> sendTo) {
    	SuperLog.sendTo = sendTo;
    }
}
