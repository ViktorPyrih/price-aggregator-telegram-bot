package ua.edu.cdu.vu.price.aggregator.telegram.bot.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.MarketplacesResponse;

@HttpExchange
public interface PriceAggregatorApiClient {

    @GetExchange("/marketplaces")
    MarketplacesResponse getMarketplaces();

    @GetExchange("/marketplaces/{marketplace}/categories")
    CategoriesResponse getCategories(@PathVariable String marketplace);

    @GetExchange("/marketplaces/{marketplace}/categories")
    CategoriesResponse getSubcategories(@PathVariable String marketplace, @RequestParam String category);

    @GetExchange("/marketplaces/{marketplace}/categories")
    CategoriesResponse getSubcategories(@PathVariable String marketplace, @RequestParam String category, @RequestParam String subcategory);

    @GetExchange("/marketplaces/{marketplace}/filters")
    FiltersResponse getFilters(@PathVariable String marketplace, @RequestParam String category, @RequestParam String subcategory1, @RequestParam String subcategory2);
}
