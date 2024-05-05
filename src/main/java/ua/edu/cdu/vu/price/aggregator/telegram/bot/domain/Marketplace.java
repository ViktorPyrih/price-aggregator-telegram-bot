package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Marketplace {

    String name;
    int subcategoriesCount;

}
