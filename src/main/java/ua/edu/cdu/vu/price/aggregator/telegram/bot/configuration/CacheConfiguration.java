package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@ConditionalOnProperty(prefix = "price-aggregator-telegram-bot.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration {
}
