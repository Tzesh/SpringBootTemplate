package com.tzesh.springtemplate.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient(org.springframework.core.env.Environment env) {
        String host = env.getProperty("spring.data.redis.host", "redis");
        String port = env.getProperty("spring.data.redis.port", "6379");
        String redisUrl = String.format("redis://%s:%s", host, port);
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);
        return Redisson.create(config);
    }
}
