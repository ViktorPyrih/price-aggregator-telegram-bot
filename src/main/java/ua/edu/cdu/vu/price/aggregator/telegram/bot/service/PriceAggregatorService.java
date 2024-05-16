package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.PriceAggregatorApiClient;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.request.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.MarketplacesResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.*;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.FilterMapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.MarketplaceMapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.ProductMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableRetry
@RequiredArgsConstructor
@Retryable(
        retryFor = WebClientResponseException.InternalServerError.class,
        maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
        backoff = @Backoff(
                delayExpression = "#{@retryProperties.delay}",
                multiplierExpression = "#{@retryProperties.multiplier}",
                maxDelayExpression = "#{@retryProperties.maxDelay}",
                randomExpression = "#{@retryProperties.random}"
        )
)
public class PriceAggregatorService {

    private final PriceAggregatorApiClient apiClient;
    private final FilterMapper filterMapper;
    private final ProductMapper productMapper;
    private final MarketplaceMapper marketplaceMapper;

    public Map<String, Marketplace> getMarketplaces() {
        MarketplacesResponse response = apiClient.getMarketplaces();
        return marketplaceMapper.convertToDomain(response).stream()
                .collect(Collectors.toMap(Marketplace::getName, Function.identity()));
    }

    public List<String> getCategories(String marketplace) {
        return apiClient.getCategories(marketplace).categories();
    }

    public List<String> getSubcategories(String marketplace, String category, Map<String, String> subcategories) {
        return apiClient.getSubcategories(marketplace, category, subcategories).categories();
    }

    public List<Filter> getFilters(String marketplace, String category, Map<String, String> subcategories) {
        FiltersResponse response = apiClient.getFilters(marketplace, category, subcategories);
        return filterMapper.convertToDomain(response.filters());
    }

    public Pageable<Product> getProducts(String marketplace, String category, Map<String, String> subcategories, List<Filter> filters, double minPrice, double maxPrice, int page) {
        ProductsRequest request = ProductsRequest.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .filters(filterMapper.convertToRequest(filters))
                .build();
        try {
            ProductsResponse response = apiClient.getProducts(marketplace, category, subcategories, request, page);
            return productMapper.convertToDomain(response);
        } catch (WebClientResponseException.TooManyRequests e) {
            throw new TooManyRequestsException(e);
        }
    }

    public Pageable<Product> search(String query) {
        ProductsResponse response = apiClient.search(query);
        return productMapper.convertToDomain(response);
    }

    public Pageable<Product> search(String marketplace, String query) {
        ProductsResponse response = apiClient.search(marketplace, query);
        return productMapper.convertToDomain(response);
    }
}
