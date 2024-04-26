package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.processor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserStateService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.BACK;

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
        if (BACK.equals(update.getMessage().getText())) {
            processInternal(userState.previousStep().previousStep(), update);
        } else {
            processInternal(userState, update);
        }
    }
    
    private void processInternal(UserState userState, Update update) throws TelegramApiException {
        var result = Optional.ofNullable(steps.get(userState.flowId()))
                .map(flowSteps -> flowSteps.get(userState.stepId()))
                .orElseThrow(() -> new IllegalStateException("There is no step found for: flowId: %d and stepId: %d".formatted(userState.flowId(), userState.stepId())))
                .processUpdate(update, userState);
        if (result.isUserStatePresent()) {
            userStateService.save(result.getUserState());
        } else {
            userStateService.delete(userState);
        }

        if (result.isUserStateAndNextStepIdPresent()) {
            processInternal(result.getUserState().withStepId(result.getNextStepId()), update);
        }
    } 
}
