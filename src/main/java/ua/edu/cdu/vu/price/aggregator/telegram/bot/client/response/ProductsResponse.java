package ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductsResponse {

    List<Product> products;
    int pagesCount;

    @Value
    @Builder
    public static class Product {
        String link;
        String image;
        String price;
        String description;
    }

}
