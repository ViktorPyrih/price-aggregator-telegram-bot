package ua.edu.cdu.vu.event.notification.telegram.bot.component.command.parser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
public class BotCommandParser {

    public String parseCommand(String command) {
        return StringUtils.substringBefore(command, SPACE);
    }
    
    public long parseLongArgument(String command) {
        return Long.parseLong(StringUtils.substringAfter(command, SPACE).split(SPACE)[0]);
    }
}
