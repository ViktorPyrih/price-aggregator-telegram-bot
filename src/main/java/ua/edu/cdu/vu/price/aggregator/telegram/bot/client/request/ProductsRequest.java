package ua.edu.cdu.vu.price.aggregator.telegram.bot.client.request;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductsRequest {

    Double minPrice;
    Double maxPrice;
    List<Filter> filters;

    @Value
    @Builder
    public static class Filter {
        String key;
        List<String> values;
    }

}
