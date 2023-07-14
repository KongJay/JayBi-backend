package com.jaychou.jaybi.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RedissonConfig
 * Package: com.jaychou.jaybi.config
 * Description:
 *
 * @Author: 红模仿
 * @Create: 2023/7/12 - 16:30
 * @Version: v1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private  Integer database;
    private  String host;
    private  Integer port;
    private  String password;
    @Bean
    public RedissonClient getRedissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://"+host+":"+ port)
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }


}
