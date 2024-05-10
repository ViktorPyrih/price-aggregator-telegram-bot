package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;

import java.util.List;

public interface TableGeneratorService<T> {

    T generateProductPriceTable(List<Product> products);
}
