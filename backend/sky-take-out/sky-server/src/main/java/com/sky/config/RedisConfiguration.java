package com.sky.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 日志
        log.info("开始创建redis模板对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置redis的连接工程对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置redis key 的序列化器 为了让图像化界面里显示的key是字符串而不是字节数组
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置redis value 的序列化器 为了让图像化界面里显示的value是字符串而不是字节数组
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }

}
