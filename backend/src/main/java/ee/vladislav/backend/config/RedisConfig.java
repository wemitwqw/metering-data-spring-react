package ee.vladislav.backend.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
		return RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofHours(24))
				.disableCachingNullValues()
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(
								new GenericJackson2JsonRedisSerializer(objectMapper))
				);
	}

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration cacheConfiguration) {
		return (builder) -> builder
				.withCacheConfiguration("rawElectricityPrices", cacheConfiguration)
				.withCacheConfiguration("formattedElectricityPrices", cacheConfiguration);
	}
}