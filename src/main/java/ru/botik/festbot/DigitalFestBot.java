package ru.botik.festbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.botik.db.DBService;
import ru.botik.db.IDBService;
import ru.botik.handlers.*;
import ru.botik.model.UserActionType;
import ru.botik.schedule.GoogleSheetsScheduleService;
import ru.botik.schedule.IScheduleService;
import ru.botik.timer.OfferCoursesTimer;
import ru.botik.timer.OfferCoursesTimerTask;

import static ru.botik.handlers.ButtonConstants.*;
import static ru.botik.handlers.MessageConstants.*;

/**
 * Интерактивное взаимодействие лучше решает TelegramWebhookBot, но в связи с возникшими при реализации проблемами
 * использую TelegramLongPollingBot. В идеале - перейти на webhook
 */
public class DigitalFestBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(DigitalFestBot.class.getName());

    private IDBService dbService;
    private IMessageSender sender;
    private IMessageHandler messageHandler;
    private IButtonHandler buttonHandler;
    private IScheduleService scheduleService;

    private final String token;
    private final String botName;

    public DigitalFestBot(String token, String botName) {
        this.token = token;
        this.botName = botName;
        init();
        updateSchedule();
        startTimer();
    }

    private void init() {
        logger.debug("Init services...");
        dbService = new DBService();
        scheduleService = new GoogleSheetsScheduleService();
        sender = new MessageSender(this);
        messageHandler = new MessageHandler(scheduleService, sender, dbService);
        buttonHandler = new ButtonHandler(scheduleService, sender, dbService);
        logger.debug("Services created successfully!");
    }

    private boolean updateSchedule() {
        return scheduleService.fillSchedules();
    }

    private void startTimer() {
        logger.debug("Starting timer task for offering courses...");
        OfferCoursesTimer tw = new OfferCoursesTimer(new OfferCoursesTimerTask(dbService, sender));
        tw.start();
        logger.debug("Timer task for offering courses started!");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            final String messageText = message.hasText() ? message.getText() : "";
            long chatId = message.getChatId();
            long userId = message.getFrom().getId();
            switch (messageText.toLowerCase()) {
                case START_MESSAGE:
                    messageHandler.processStartMessage(update.getMessage(), getBotUsername());
                    break;
                case UPDATE_SCHEDULE_MESSAGE:
                    boolean success = updateSchedule();
                    if (!success) {
                        sender.sendMessage(chatId, UPDATE_SCHEDULE_ERROR);
                    }
                    break;
                case KEYBOARD_MESSAGE:
                    messageHandler.processKeyboardMessage(message);
                    break;
                default:
                    UserActionType userAction = dbService.getUserAction(userId);
                    switch (userAction) {
                        case AGE_INPUT:
                            messageHandler.processAgeInputMessage(message);
                            break;
                        case SCHEDULE_ACCESS:
                            messageHandler.processScheduleAccessMessage(message);
                            break;
                        case WORD_GUESSING:
                            messageHandler.processWordGuessingMessage(message);
                            break;
                        case FEST_EVALUATION_LIKE:
                        case FEST_EVALUATION_DISLIKE:
                            messageHandler.processFestEndingMessage(message);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            String data = callbackQuery.getData();
            if (data.startsWith(YES_BUTTON)) {
                if (data.endsWith(LIKE_BUTTON)) {
                    buttonHandler.processLikeEventButton(message);
                } else {
                    buttonHandler.processYesButton(message, data);
                }
            } else if (data.startsWith(NO_BUTTON)) {
                if (data.endsWith(DISLIKE_BUTTON)) {
                    buttonHandler.processDislikeEventButton(message);
                } else {
                    buttonHandler.processNoButton(message);
                }
            } else if (data.startsWith(ACTIVITY_BUTTON)) {
                if (data.endsWith("no")) {
                    buttonHandler.processNoActivityButton(message);
                } else {
                    buttonHandler.processActivityButton(message, data);
                }
            } else if (data.startsWith(COURSE_BUTTON)) {
                buttonHandler.processCoursesButton(message, data);
            } else if (CHILD_BUTTON.equals(data) || PARENT_BUTTON.equals(data)) {
                buttonHandler.processParentChildButton(message, data);
            } else if (CANCEL_BUTTON.equals(data)) {
                buttonHandler.processCancelButton(message);
            }

        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }


}

