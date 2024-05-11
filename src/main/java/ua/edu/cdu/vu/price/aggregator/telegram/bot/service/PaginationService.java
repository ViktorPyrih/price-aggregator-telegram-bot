package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final String GAP_MARKER = "...";

    public List<String> pages(int page, int totalPages) {
        List<String> pages = new LinkedList<>();
        if (totalPages <= 6 || (totalPages <= 9 && totalPages - page < 5)) {
            pages.addAll(iterate(1, totalPages));
        } else if (totalPages - page >= 5 && page <= 5) {
            pages.addAll(iterate(1, Math.max(page + 1, 3)));
            pages.add(GAP_MARKER);
            pages.addAll(iterate(totalPages - 2, totalPages));
        } else if (totalPages - page <= 4) {
            pages.addAll(iterate(1, 3));
            pages.add(GAP_MARKER);
            pages.addAll(iterate(Math.min(totalPages - 2, page - 1), totalPages));
        } else {
            pages.addAll(iterate(1, 3));
            pages.add(GAP_MARKER);
            pages.addAll(iterate(page - 1, page + 1));
            pages.add(GAP_MARKER);
            pages.addAll(iterate(totalPages - 2, totalPages));
        }

        return pages;
    }

    private List<String> iterate(int from, int to) {
        return IntStream.rangeClosed(from, to)
                .mapToObj(String::valueOf)
                .toList();
    }
}
