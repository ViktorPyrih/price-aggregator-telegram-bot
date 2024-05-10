package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {

    String link;
    String image;
    String priceImage;
    String descriptionImage;
    String title;
    String price;

}
