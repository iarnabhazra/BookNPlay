package com.booknplay.turf.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic onboardingTopic() {
        return new NewTopic("onboarding-events", 3, (short)1);
    }
}
