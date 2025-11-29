package com.booknplay.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {
    @Bean
    LettuceConnectionFactory redisConnectionFactory() { return new LettuceConnectionFactory(); }
    @Bean
    RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String,Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        return tpl;
    }
}
