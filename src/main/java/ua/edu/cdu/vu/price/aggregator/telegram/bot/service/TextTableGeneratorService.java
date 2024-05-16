package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import com.github.freva.asciitable.AsciiTable;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;

import java.util.List;

@Service
public class TextTableGeneratorService implements TableGeneratorService<List<String>> {

    private static final String[] PRODUCT_PRICE_HEADER = new String[]{"Marketplace", "Product", "Price"};

    private static final int MAX_TABLE_ROWS = 10;

    private static final String PRE_TAG = "<pre>";
    private static final String CLOSING_PRE_TAG = "</pre>";

    @Override
    public List<String> generateProductPriceTable(List<Product> products) {
        return Lists.partition(products, MAX_TABLE_ROWS).stream()
                .map(this::generateProductPriceTableAsString)
                .toList();
    }

    private String generateProductPriceTableAsString(List<Product> products) {
        return PRE_TAG + AsciiTable.getTable(PRODUCT_PRICE_HEADER, convertToMatrix(products)) + CLOSING_PRE_TAG;
    }

    private Object[][] convertToMatrix(List<Product> products) {
        return products.stream()
                .map(product -> new Object[] {product.getMarketplace(), product.getTitle(), product.getPrice()})
                .toArray(Object[][]::new);
    }
}
