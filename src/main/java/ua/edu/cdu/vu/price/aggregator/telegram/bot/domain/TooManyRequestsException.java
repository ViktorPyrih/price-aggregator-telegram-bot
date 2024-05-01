package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

public class TooManyRequestsException extends RuntimeException {

    private static final String MESSAGE = "Too many requests. Please try again later";

    public TooManyRequestsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
