package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import java.util.List;

public record Pageable<T>(List<T> content, int pagesCount) {
}
