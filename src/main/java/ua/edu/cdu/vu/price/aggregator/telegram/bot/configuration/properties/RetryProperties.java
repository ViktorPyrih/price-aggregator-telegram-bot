package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "price-aggregator-telegram-bot.retry")
public class RetryProperties {

    private int maxAttempts = 3;
    private long delay = 1000;
    private double multiplier = 2.0;
    private long maxDelay = 10000;
    private boolean random = true;

}
