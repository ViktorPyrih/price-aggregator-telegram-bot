package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "price-aggregator-telegram-bot.configuration")
public class PriceAggregatorTelegramBotConfiguration {

    @NotBlank
    private String token;
    @NotBlank
    private String username;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
