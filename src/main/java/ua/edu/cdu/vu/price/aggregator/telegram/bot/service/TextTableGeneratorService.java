package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import com.github.freva.asciitable.AsciiTable;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;

import java.util.List;

@Service
public class TextTableGeneratorService implements TableGeneratorService<String> {

    private static final String[] PRODUCT_PRICE_HEADER = new String[]{"Product", "Price"};

    private static final String PRE_TAG = "<pre>";
    private static final String CLOSING_PRE_TAG = "</pre>";

    @Override
    public String generateProductPriceTable(List<Product> products) {
        return PRE_TAG + AsciiTable.getTable(PRODUCT_PRICE_HEADER, convertToMatrix(products)) + CLOSING_PRE_TAG;
    }

    private Object[][] convertToMatrix(List<Product> products) {
        return products.stream()
                .map(product -> new Object[] {product.getTitle(), product.getPrice()})
                .toArray(Object[][]::new);
    }
}
