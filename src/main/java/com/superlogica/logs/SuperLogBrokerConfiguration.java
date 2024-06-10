package com.superlogica.logs;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


@Configuration
public class SuperLogBrokerConfiguration {
    
    @Bean
    public Producer<String, String> producer(
        @Value("${kafka.bootstrap.servers}") String bootstrapServers,

        @Value("${kafka.sasl.mechanism:PLAIN}") String saslMechanism,
        @Value("${kafka.security.protocol:PLAINTEXT}") String securityProtocol,
        @Value("${kafka.sasl.jaas.config:}") String saslJaasConfig
    ) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.ACKS_CONFIG, "1");
		props.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, "10");
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");
        props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, "300000");
        props.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class
        );
        props.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
		  StringSerializer.class
        );
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);

        if (StringUtils.equalsIgnoreCase(securityProtocol, "SASL_SSL")) {
            props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }

		return new KafkaProducer<>(props);
    }
}