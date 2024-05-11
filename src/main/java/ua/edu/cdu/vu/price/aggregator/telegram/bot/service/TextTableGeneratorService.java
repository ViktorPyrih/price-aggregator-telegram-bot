package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import com.github.freva.asciitable.AsciiTable;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Service
public class TextTableGeneratorService implements TableGeneratorService<List<String>> {

    private static final String[] PRODUCT_PRICE_HEADER = new String[]{"Marketplace", "Product", "Price"};

    private static final int MAX_TITLE_PARTS = 5;
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
                .map(product -> new Object[] {product.getMarketplace(), trim(product.getTitle()), product.getPrice()})
                .toArray(Object[][]::new);
    }

    private String trim(String title) {
        return Arrays.stream(title.split(SPACE))
                .limit(MAX_TITLE_PARTS)
                .collect(Collectors.joining(SPACE));
    }
}
