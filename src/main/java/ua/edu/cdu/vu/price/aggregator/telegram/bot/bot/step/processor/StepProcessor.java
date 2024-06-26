package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserStateService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.task.TelegramSpinnerTask;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class StepProcessor {

    private static final Set<String> COMMANDS = Set.of(BACK, COMPLETE, RESET);

    private final UserStateService userStateService;
    private final TelegramSenderService telegramSenderService;
    private final ScheduledExecutorService taskScheduler;
    private final Map<Integer, Map<Integer, Step>> steps;

    @Value("${price-aggregator-telegram-bot.scheduling.tasks.spinner.delay-seconds:5}")
    private int spinnerTaskDelaySeconds;

    public StepProcessor(UserStateService userStateService, TelegramSenderService telegramSenderService, ScheduledExecutorService taskScheduler, List<Step> steps) {
        this.userStateService = userStateService;
        this.telegramSenderService = telegramSenderService;
        this.taskScheduler = taskScheduler;
        this.steps = steps.stream()
                .collect(groupingBy(Step::flowId, toMap(Step::stepId, Function.identity())));
    }

    public void process(Update update, UserState userState) throws TelegramApiException {
        process(update, userState, false);
    }

    public void process(Update update, UserState userState, boolean isInitial) throws TelegramApiException {
        runWithSpinner(getChatId(update), () -> {
            try {
                if (isInitial || EXIT.equals(update.getMessage().getText())) {
                    Step step = getStep(userState.initialStep());
                    onStart(update, userState.initialStep(), step, true);
                } else if (BACK.equals(update.getMessage().getText())) {
                    UserState previousStepUserState = userState.previousStep();
                    processStep(update, previousStepUserState);
                } else {
                    processStep(update, userState);
                }
            } catch (TelegramApiException e) {
                userStateService.save(userState);
                throw e;
            }
        });
    }

    private void runWithSpinner(long chatId, TelegramApiRunnable runnable) throws TelegramApiException {
        TelegramSpinnerTask task = new TelegramSpinnerTask(chatId, telegramSenderService);
        var future = taskScheduler.schedule(task, spinnerTaskDelaySeconds, TimeUnit.SECONDS);

        try {
            runnable.run();
        } finally {
            future.cancel(true);
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
            if (!step.isFinal() && !COMMANDS.contains(text)) {
                onStartNextStep(update, newUserState);
            }
        } else {
            userStateService.delete(userState);
        }
    }

    private void onStart(Update update, UserState userState, Step step, boolean isInitial) throws TelegramApiException {
        Step.Result result = step.onStart(update, userState);
        UserState newUserState = result.getUserState();

        if (isInitial || userState != newUserState) {
            userStateService.save(newUserState);
        }

        if (userState.stepId() != newUserState.stepId()) {
            onStartNextStep(update, newUserState);
        }
    }

    private void onStartNextStep(Update update, UserState userState) throws TelegramApiException {
        var nextStep = findStep(userState);
        if (nextStep.isPresent()) {
            onStart(update, userState, nextStep.get(), false);
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
