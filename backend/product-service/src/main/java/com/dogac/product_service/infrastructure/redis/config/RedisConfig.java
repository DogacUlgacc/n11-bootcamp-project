package com.dogac.product_service.infrastructure.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

@Configuration
public class RedisConfig {

        @Bean
        public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
                ObjectMapper redisObjectMapper = objectMapper.copy();
                redisObjectMapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.EVERYTHING,
                                JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                                redisObjectMapper);

                return RedisCacheConfiguration.defaultCacheConfig()
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(serializer));
        }
}
