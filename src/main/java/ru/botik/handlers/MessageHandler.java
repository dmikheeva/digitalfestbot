package ru.botik.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.botik.db.IDBService;
import ru.botik.festbot.FestBotUtils;
import ru.botik.model.FestActivity;
import ru.botik.schedule.GoogleSheetsScheduleService;
import ru.botik.schedule.IScheduleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static ru.botik.handlers.ButtonConstants.*;

/**
 * Created by Daria on 16.04.2018.
 */
public class MessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(MessageHandler.class.getName());

    private IScheduleService scheduleManager;
    private IMessageSender sender;
    private IDBService dbService;

    public MessageHandler(IScheduleService scheduleManager, IMessageSender sender, IDBService dbService) {
        this.sender = sender;
        this.dbService = dbService;
        this.scheduleManager = scheduleManager;
    }

    @Override
    public void processStartMessage(Message message, String botName) {
        String userName = getUserFirstName(message);
        sender.sendSticker(message.getChatId(), StickerConstants.STICKER_WAZZUP);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        InlineKeyboardButton childButton = new InlineKeyboardButton();
        childButton.setText(ButtonConstants.I_AM_CHILD_BUTTON);
        childButton.setCallbackData(CHILD_BUTTON);
        buttons.add(childButton);
        InlineKeyboardButton parentButton = new InlineKeyboardButton();
        parentButton.setText(ButtonConstants.I_AM_PARENT_BUTTON);
        parentButton.setCallbackData(PARENT_BUTTON);
        buttons.add(parentButton);
        allButtons.add(buttons);
        markup.setKeyboard(allButtons);

        sender.sendMessage(message.getChatId(),
                String.format(MessageConstants.HELLO_MESSAGE, (userName.isEmpty() ? "" : ", " + userName), botName),
                markup);
    }

    private String getUserFirstName(Message message) {
        if (message.getFrom() != null && message.getFrom().getFirstName() != null) {
            return message.getFrom().getFirstName();
        }
        return "";
    }

    @Override
    public void processScheduleAccessMessage(Message message) {
        final String messageText = message.hasText() ? message.getText() : "";
        long chatId = message.getChatId();
        if (MessageConstants.LECTURE_CATEGORY.equals(messageText)) {
            showActualActivities(chatId, true);
        } else if (MessageConstants.OTHER_CATEGORY.equals(messageText)) {
            showActualActivities(chatId, false);
        } else if (MessageConstants.MY_POINTS.equals(messageText)) {
            sendMyPointsMessage(chatId);
        }
    }

    private void showActualActivities(long chatId, boolean isLecture) {
        int age = 0;
        if (isLecture) {
            age = dbService.getAge(chatId);
        }
        List<FestActivity> activities = isLecture ?
                GoogleSheetsScheduleService.findSuitableActivities(scheduleManager.getFestLecturesSchedule(), age) :
                scheduleManager.getFestGameSchedule();
        if (activities.size() == 0) {
            sender.sendMessage(chatId, MessageConstants.NO_MORE_LECTURES_MESSAGE);
            return;
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        InlineKeyboardButton b;
        for (FestActivity activity : activities) {
            if (activity.getName() != null && !activity.getName().isEmpty()) {
                List<InlineKeyboardButton> buttons = new ArrayList<>();
                b = new InlineKeyboardButton();
                b.setText(activity.getName());
                b.setCallbackData(ACTIVITY_BUTTON + activity.getId());
                buttons.add(b);
                allButtons.add(buttons);
            }
        }
        if (isLecture) {
            b = new InlineKeyboardButton();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            b.setText(ButtonConstants.DISLIKE_EVENTS_BUTTON);
            b.setCallbackData("activityButton:no");
            buttons.add(b);
            allButtons.add(buttons);
        }

        markup.setKeyboard(allButtons);
        sender.sendMessage(chatId,
                isLecture ? MessageConstants.CHOOSE_LECTURE_MESSAGE : MessageConstants.CHOOSE_GAME_MESSAGE,
                markup);
    }

    private void sendMyPointsMessage(long chatId) {
        int correctWordsAmount = dbService.getPoints(chatId);
        sender.sendMessage(chatId,
                String.format(MessageConstants.CHECK_SCORE_MESSAGE, correctWordsAmount) +
                        (correctWordsAmount > 0 ? MessageConstants.GET_PRESENT_MESSAGE : MessageConstants.GET_POINTS_MESSAGE));
    }

    @Override
    public void processWordGuessingMessage(Message message) {
        int activityId = dbService.getUserActionActivityId(message.getChatId());
        FestActivity activity = FestBotUtils.getActivityById(scheduleManager, activityId);
        if (activity == null) {
            return;
        }
        String word = message.getText();
        if (word != null && activity.getSecret() != null &&
                word.toLowerCase().equals(activity.getSecret().toLowerCase())) {
            //правильно отгадал слово
            int correctWordsAmount = dbService.setCorrectWord(message.getChatId(), activityId, word);
            if (correctWordsAmount > 0) {
                sendRightAnswerMessage(message.getChatId(), correctWordsAmount);
            }
        } else {
            //что-то пошло не так
            sendWrongAnswerMessage(message.getChatId());
        }

    }

    private void sendRightAnswerMessage(long chatId, int correctWordsAmount) {
        sender.sendSticker(chatId, getRandomSticker(true));
        sender.sendMessage(chatId, String.format(MessageConstants.CORRECT_ANSWER_MESSAGE, correctWordsAmount));
        sender.sendMessage(chatId, MessageConstants.CHOOSE_LECTURE_AFTER_CORRECT_WORD_MESSAGE,
                FestBotUtils.createChooseEventKeyboard());
    }

    private void sendWrongAnswerMessage(long chatId) {
        sender.sendSticker(chatId, getRandomSticker(false));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText(MessageConstants.CANCEL_ANSWER);
        cancelButton.setCallbackData("cancel");
        buttons.add(cancelButton);
        allButtons.add(buttons);
        markup.setKeyboard(allButtons);

        sender.sendMessage(chatId, MessageConstants.WRONG_CHECK_WORD_MESSAGE, markup);

    }

    private String getRandomSticker(boolean isHappy) {
        //todo
        Random rnd = new Random();
        List<String> stickers = isHappy ?
                Arrays.asList(StickerConstants.STICKER_BU_DUM_TSS, StickerConstants.STICKER_CLAP_CLAP,
                        StickerConstants.STICKER_GOOD, StickerConstants.STICKER_YESS, StickerConstants.STICKER_YOU_WIN) :
                Arrays.asList(StickerConstants.STICKER_SAD, StickerConstants.STICKER_CRYING,
                        StickerConstants.STICKER_DO_NOT_KNOW);
        int i = rnd.nextInt(stickers.size());
        return stickers.get(i);
    }

    @Override
    public void processAgeInputMessage(Message message) {
        final String ageText = message.hasText() ? message.getText() : "";
        long chatId = message.getChatId();
        try {
            int age = Integer.parseInt(ageText);
            if (age >= 8 && age <= 17) {
                dbService.setChildAge(message.getFrom().getId(), age);
                sender.sendMessage(message.getChatId(), MessageConstants.CHOOSE_SCHEDULE_MESSAGE,
                        FestBotUtils.createChooseEventKeyboard());
            } else {
                sender.sendMessage(chatId, MessageConstants.WRONG_AGE_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(chatId, MessageConstants.WRONG_AGE_MESSAGE);
            logger.debug("Wrong format age '" + ageText + "' from user: " + chatId);
        }
    }

    @Override
    public void processFestEndingMessage(Message message) {
        sender.sendMessage(message.getChatId(), MessageConstants.END_FEST_MESSAGE, new ReplyKeyboardRemove());
    }

    @Override
    public void processKeyboardMessage(Message message) {
        sender.sendMessage(message.getChatId(), MessageConstants.CHOOSE_SCHEDULE_MESSAGE,
                FestBotUtils.createChooseEventKeyboard());
    }
}
