package ua.edu.cdu.vu.event.notification.telegram.bot.component.command.resolver;

import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.Command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class BotCommandResolver {

    private static final String DEFAULT_COMMAND_NAME = "/";

    private final Map<String, BotCommand> commands;

    public BotCommandResolver(List<BotCommand> commands) {
        this.commands = commands.stream()
                .collect(Collectors.toMap(this::toTelegramCommand, Function.identity()));
    }

    private String toTelegramCommand(Object command) {
        Command commandAnnotation = command.getClass().getAnnotation(Command.class);

        if (isNull(commandAnnotation)) {
            throw new IllegalStateException("Bot command: %s should be annotated with %s annotation".formatted(command.getClass().getSimpleName(), Command.class.getName()));
        }

        return commandAnnotation.value();
    }

    public BotCommand resolve(String command) {
        return commands.getOrDefault(command, commands.get(DEFAULT_COMMAND_NAME));
    }
}
