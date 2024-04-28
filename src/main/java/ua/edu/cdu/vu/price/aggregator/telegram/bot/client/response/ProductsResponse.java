package ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response;

import java.util.List;

public record ProductsResponse(List<Product> products, int pagesCount) {

    public record Product(String link, String image, String price, String description) {
    }
}
