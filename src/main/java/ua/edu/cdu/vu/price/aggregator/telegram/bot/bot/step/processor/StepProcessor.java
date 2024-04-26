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
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.COMPLETE;

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
        process(userState, update, false);
    }

    public void process(UserState userState, Update update, boolean isInitial) throws TelegramApiException {
        if (isInitial) {
            Step step = getStep(userState);
            userStateService.save(userState);
            step.onStart(update, userState);
        } else if (BACK.equals(update.getMessage().getText())) {
            UserState previousStepUserState = userState.previousStep();
            Step step = getStep(previousStepUserState);
            process(previousStepUserState, update, step);
        } else {
            Step step = getStep(userState);
            process(userState, update, step);
        }
    }

    private void process(UserState userState, Update update, Step step) throws TelegramApiException {

        var result = step.processUpdate(update, userState);

        if (result.isUserStateAndNextStepIdPresent()) {
            UserState newUserState = result.getUserState().withStepId(result.getNextStepId());
            process(newUserState, update, getStep(newUserState));
        } else if (result.isUserStatePresent()) {
            UserState newUserState = result.getUserState();
            userStateService.save(newUserState);

            String text = update.getMessage().getText();
            if (!BACK.equals(text) && !COMPLETE.equals(text)) {
                var nextStep = findStep(newUserState);
                if (nextStep.isPresent()) {
                    nextStep.get().onStart(update, newUserState);
                }
            }
        } else {
            userStateService.delete(userState);
        }
    }

    private Step getStep(UserState userState) {
        return findStep(userState)
                .orElseThrow(() -> new IllegalStateException("There is no step found for: flowId: %d and stepId: %d".formatted(userState.flowId(), userState.stepId())));
    }

    private Optional<Step> findStep(UserState userState) {
        return Optional.ofNullable(steps.get(userState.flowId()))
                .map(flowSteps -> flowSteps.get(userState.stepId()));
    }
}
