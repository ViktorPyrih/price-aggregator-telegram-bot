package ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FilterMapper {

    List<Filter> convertToDomain(List<FiltersResponse.Filter> filters);

    Filter convertToDomain(FiltersResponse.Filter filter);
}
