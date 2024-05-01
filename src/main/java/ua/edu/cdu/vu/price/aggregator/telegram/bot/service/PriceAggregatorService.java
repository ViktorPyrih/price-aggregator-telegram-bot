package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.PriceAggregatorApiClient;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.request.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Pageable;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.FilterMapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.ProductMapper;

import java.util.List;

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

    @Cacheable("marketplaces")
    public List<String> getMarketplaces() {
        return apiClient.getMarketplaces().marketplaces();
    }

    @Cacheable("categories")
    public List<String> getCategories(String marketplace) {
        return apiClient.getCategories(marketplace).categories();
    }

    @Cacheable("subcategories1")
    public List<String> getSubcategories(String marketplace, String category) {
        return apiClient.getSubcategories(marketplace, category).categories();
    }

    @Cacheable("subcategories2")
    public List<String> getSubcategories(String marketplace, String category, String subcategory) {
        return apiClient.getSubcategories(marketplace, category, subcategory).categories();
    }

    @Cacheable("filters")
    public List<Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        FiltersResponse response = apiClient.getFilters(marketplace, category, subcategory1, subcategory2);
        return filterMapper.convertToDomain(response.filters());
    }

    public Pageable<Product> getProducts(String marketplace, String category, String subcategory1, String subcategory2, List<Filter> filters, double minPrice, double maxPrice, int page) {
        ProductsRequest request = ProductsRequest.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .filters(filterMapper.convertToRequest(filters))
                .build();
        ProductsResponse response = apiClient.getProducts(marketplace, category, subcategory1, subcategory2, request, page);

        return productMapper.convertToDomain(response);
    }

    public Pageable<Product> search(String query) {
        ProductsResponse response = apiClient.search(query);
        return productMapper.convertToDomain(response);
    }
}
