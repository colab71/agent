package com.example.aispringboot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.database}")
    private Integer database;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        String address = "redis://"+redisHost+":"+redisPort;
        config.useSingleServer().setAddress(address).setPassword(redisPassword).setDatabase(database);

        return Redisson.create(config);
    }
}
