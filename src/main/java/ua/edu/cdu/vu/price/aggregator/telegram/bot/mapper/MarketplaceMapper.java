package ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.MarketplacesResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Marketplace;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface MarketplaceMapper {

    default List<Marketplace> convertToDomain(MarketplacesResponse response) {
        return response.marketplaces().stream()
                .map(this::convertToDomain)
                .toList();
    }

    Marketplace convertToDomain(MarketplacesResponse.Marketplace marketplace);
}
