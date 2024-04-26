package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Filter {

    String key;
    List<String> values;

}
