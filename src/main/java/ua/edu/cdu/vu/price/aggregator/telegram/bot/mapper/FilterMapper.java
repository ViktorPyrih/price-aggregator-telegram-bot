package ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.request.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;

import java.util.List;
import java.util.Map;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FilterMapper {

    List<Filter> convertToDomain(List<FiltersResponse.Filter> filters);

    Filter convertToDomain(FiltersResponse.Filter filter);

    default List<Filter> convertToDomain(Map<String, List<String>> filters) {
        return filters.entrySet().stream()
                .map(entry -> Filter.builder()
                        .key(entry.getKey())
                        .values(entry.getValue())
                        .build())
                .toList();
    }

    List<ProductsRequest.Filter> convertToRequest(List<Filter> filter);

    ProductsRequest.Filter convertToRequest(Filter filter);
}
