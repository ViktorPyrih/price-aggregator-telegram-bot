package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.parser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
public class BotCommandParser {

    public String parseCommand(String command) {
        return StringUtils.substringBefore(command, SPACE);
    }

    public String[] parseArguments(String data) {
        return StringUtils.substringAfter(data, SPACE).split(SPACE);
    }
}
