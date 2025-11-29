package com.booknplay.booking.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    @ConditionalOnMissingBean(name = "bookingTopic")
    public NewTopic bookingTopic() {
        return new NewTopic("booking-events", 3, (short)1);
    }
}
