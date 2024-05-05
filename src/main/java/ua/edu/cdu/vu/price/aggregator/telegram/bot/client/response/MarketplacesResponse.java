package ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response;

import java.util.List;

public record MarketplacesResponse(List<Marketplace> marketplaces) {

    public record Marketplace(String name, int subcategoriesCount) {
    }
}
