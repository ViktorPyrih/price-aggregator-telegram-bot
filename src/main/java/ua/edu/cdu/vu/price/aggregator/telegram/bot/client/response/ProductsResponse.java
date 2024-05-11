package ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response;

import java.util.List;

public record ProductsResponse(List<Product> products, int pagesCount) {

    public record Product(String marketplace, String link, String image, String priceImage, String descriptionImage, String title, String price) {
    }
}
