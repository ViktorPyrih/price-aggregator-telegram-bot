package ua.edu.cdu.vu.event.notification.telegram.bot.component.step.processor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.event.notification.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.event.notification.telegram.bot.service.UserStateService;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.step.Step;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Component
public class StepProcessor {

    private final UserStateService userStateService;
    private final Map<Integer, Map<Integer, Step>> steps;

    public StepProcessor(UserStateService userStateService, List<Step> steps) {
        this.userStateService = userStateService;
        this.steps = steps.stream()
                .collect(groupingBy(Step::flowId, toMap(Step::stepId, Function.identity())));
    }

    public void process(UserState userState, Update update) throws TelegramApiException {
        Optional<UserState> updatedUserState = Optional.ofNullable(steps.get(userState.getFlowId()))
                .map(flowSteps -> flowSteps.get(userState.getStepId()))
                .orElseThrow(() -> new IllegalStateException("There is now step found for: flowId: %d and stepId: %d".formatted(userState.getFlowId(), userState.getStepId())))
                .process(update, userState);
        if (updatedUserState.isEmpty()) {
            userStateService.delete(userState);
        } else {
            userStateService.save(updatedUserState.get());
        }
    }
}
