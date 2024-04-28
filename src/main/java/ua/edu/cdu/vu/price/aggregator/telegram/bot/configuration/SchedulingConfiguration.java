package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {

    @Value("${spring.scheduling.pool.size:10}")
    private int corePoolSize;

    @Bean
    public ScheduledExecutorService taskScheduler() {
        return Executors.newScheduledThreadPool(corePoolSize);
    }
}
