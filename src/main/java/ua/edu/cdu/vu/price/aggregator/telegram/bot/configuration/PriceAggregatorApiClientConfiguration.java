package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.PriceAggregatorApiClient;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration.properties.ApiClientProperties;

@Configuration
public class PriceAggregatorApiClientConfiguration {

    private static final int MAX_IN_MEMORY_SIZE_BYTES = 10 * 1024 * 1024;
    private static final String X_API_KEY = "x-api-key";

    @Bean
    public PriceAggregatorApiClient priceAggregatorApiClient(WebClient priceAggregatorApiWebClient) {
        return HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(priceAggregatorApiWebClient))
                .build().createClient(PriceAggregatorApiClient.class);
    }

    @Bean
    public WebClient priceAggregatorWebClient(ApiClientProperties priceAggregatorApiClientProperties) {
        return WebClient.builder()
                .baseUrl(priceAggregatorApiClientProperties.getUrl())
                .defaultHeader(X_API_KEY, priceAggregatorApiClientProperties.getApiKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES))
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "price-aggregator-telegram-bot.clients.price-aggregator-api")
    public ApiClientProperties priceAggregatorApiClientProperties() {
        return new ApiClientProperties();
    }
}
