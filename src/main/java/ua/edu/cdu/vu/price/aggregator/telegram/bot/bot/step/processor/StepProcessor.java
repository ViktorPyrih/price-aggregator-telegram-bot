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
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;

@Component
public class StepProcessor {

    private static final Set<String> COMMANDS = Set.of(BACK, COMPLETE, RESET);

    private final UserStateService userStateService;
    private final Map<Integer, Map<Integer, Step>> steps;

    public StepProcessor(UserStateService userStateService, List<Step> steps) {
        this.userStateService = userStateService;
        this.steps = steps.stream()
                .collect(groupingBy(Step::flowId, toMap(Step::stepId, Function.identity())));
    }

    public void process(Update update, UserState userState) throws TelegramApiException {
        process(update, userState, false);
    }

    public void process(Update update, UserState userState, boolean isInitial) throws TelegramApiException {
        if (isInitial) {
            Step step = getStep(userState);
            onStart(update, userState, step);
        } else if (BACK.equals(update.getMessage().getText())) {
            UserState previousStepUserState = userState.previousStep();
            processStep(update, previousStepUserState);
        } else {
            processStep(update, userState);
        }
    }

    private void processStep(Update update, UserState userState) throws TelegramApiException {
        Step step = getStep(userState);
        process(update, userState, step);
    }

    private void process(Update update, UserState userState, Step step) throws TelegramApiException {

        var result = step.processUpdate(update, userState);

        if (result.isUserStateAndNextStepIdPresent()) {
            UserState newUserState = result.getUserState().withStepId(result.getNextStepId());
            process(update, newUserState, getStep(newUserState));
        } else if (result.isUserStatePresent()) {
            UserState newUserState = result.getUserState();
            userStateService.save(newUserState);

            String text = update.getMessage().getText();
            if (!COMMANDS.contains(text) && newUserState.stepId() != userState.stepId()) {
                var nextStep = findStep(newUserState);
                if (nextStep.isPresent()) {
                    onStart(update, newUserState, nextStep.get());
                }
            }
        } else {
            userStateService.delete(userState);
        }
    }

    private void onStart(Update update, UserState userState, Step step) throws TelegramApiException {
        UserState newUserState = step.onStart(update, userState).getUserState();
        userStateService.save(newUserState);
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
