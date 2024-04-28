package ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.client.response.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Pageable;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductMapper {

    @Mapping(target = "content", source = "products")
    Pageable<Product> convertToDomain(ProductsResponse productsResponse);

    List<Product> convertToDomain(List<ProductsResponse.Product> products);

    Product convertToDomain(ProductsResponse.Product product);
}
