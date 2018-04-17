package ru.botik.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.botik.db.IDBService;
import ru.botik.festbot.FestBotUtils;
import ru.botik.model.ActivityType;
import ru.botik.model.FestActivity;
import ru.botik.model.FestCourse;
import ru.botik.model.UserActionType;
import ru.botik.schedule.IScheduleService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.botik.handlers.MessageConstants.GAME_DESCRIPTION__MESSAGE;
import static ru.botik.handlers.MessageConstants.LECTURE_DESCRIPTION_MESSAGE;

/**
 * Created by Daria on 16.04.2018.
 */
public class ButtonHandler implements IButtonHandler {
    private static final Logger logger = LogManager.getLogger(ButtonHandler.class.getName());
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private final IScheduleService scheduleManager;
    private final IMessageSender sender;
    private final IDBService dbService;

    public ButtonHandler(IScheduleService scheduleManager, IMessageSender sender, IDBService dbService) {
        this.sender = sender;
        this.dbService = dbService;
        this.scheduleManager = scheduleManager;
    }

    @Override
    public void processLikeEventButton(Message message) {
        sender.editMessage(message, message.getText());
        int age = dbService.getAge(message.getChatId());
        List<FestCourse> suitable = scheduleManager
                .getFestCourses()
                .stream()
                .filter(fc -> fc.getStartAge() <= age && fc.getEndAge() >= age)
                .collect(Collectors.toList());
        if (suitable.size() != 0) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> buttons;
            List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();

            for (FestCourse fc : suitable) {
                InlineKeyboardButton courseButton = new InlineKeyboardButton();
                courseButton.setText(fc.getName());
                courseButton.setCallbackData(ButtonConstants.COURSE_BUTTON + ":" + fc.getId());
                buttons = new ArrayList<>();
                buttons.add(courseButton);
                allButtons.add(buttons);
            }
            markup.setKeyboard(allButtons);

            sender.sendMessage(message.getChatId(), MessageConstants.WAIT_YOU_LATER_MESSAGE, markup);
        }
        sendLeaveContactsMessage(message.getChatId());
        sendContactsInfo(message.getChatId());
        dbService.setUserAction(message.getChatId(), UserActionType.FEST_EVALUATION_LIKE);
    }

    private void sendLeaveContactsMessage(long chatId) {
        String s = String.format(MessageConstants.GET_DISCOUNT_MESSAGE, "http://mailru.codabra.org/course_worksheet");
        sender.sendMessage(chatId, s, null);
    }

    private void sendContactsInfo(long chatId) {
        String s = MessageConstants.CONTACTS_MESSAGE;
        sender.sendMessage(chatId, s, null);
        sendSeeYouAgainMessage(chatId);
    }

    @Override
    public void processYesButton(Message message, String data) {
        String activityIdStr = data.replace(ButtonConstants.YES_BUTTON + ":", "");
        int activityId = Integer.parseInt(activityIdStr);
        FestActivity activity = FestBotUtils.getActivityById(scheduleManager, activityId);
        if (activity == null) {
            logger.error("No activity found! id: " + activityId);
            return;
        }
        long chatId = message.getChatId();
        sender.editMessage(message, message.getText().substring(0, message.getText().indexOf(".")));
        boolean isWordGuessed = dbService.isWordGuessed(chatId, activityId);
        if (isWordGuessed) {
            sender.sendMessage(chatId, MessageConstants.ALREADY_CHECKED_WORD_MESSAGE);
            sendChooseScheduleMessage(chatId);
        } else {
            dbService.setUserAction(chatId, UserActionType.WORD_GUESSING, activityId);
            sender.sendMessage(message.getChatId(), MessageConstants.ENTER_CHECK_WORD_MESSAGE, new ReplyKeyboardRemove());
        }
    }

    private void sendChooseScheduleMessage(long chatId) {
        sender.sendMessage(chatId, MessageConstants.CHOOSE_SCHEDULE_MESSAGE, FestBotUtils.createChooseEventKeyboard());
    }

    @Override
    public void processNoButton(Message message) {
        //todo изменять сообщение более разумным способом
        sender.editMessage(message, message.getText().substring(0, message.getText().indexOf(".") + 1));
        sender.sendMessage(message.getChatId(), MessageConstants.CHOOSE_LECTURE_AFTER_CORRECT_WORD_MESSAGE);
    }

    @Override
    public void processDislikeEventButton(Message message) {
        //todo
        sender.editMessage(message, message.getText());
        sendSeeYouAgainMessage(message.getChatId());
        dbService.setUserAction(message.getChatId(), UserActionType.FEST_EVALUATION_DISLIKE);
    }

    private void sendSeeYouAgainMessage(long chatId) {
        sender.sendMessage(chatId, MessageConstants.SEE_YOU_LATER_MESSAGE, null);
        sender.sendSticker(chatId, StickerConstants.STICKER_GOOD_LUCK);
    }

    @Override
    public void processParentChildButton(Message message, String data) {
        long chatId = message.getChatId();
        boolean isChild = ButtonConstants.CHILD_BUTTON.equals(data);
        dbService.createUser(chatId,
                isChild,
                message.getChat().getUserName());
        //todo
        sender.editMessage(message, message.getText().substring(0, message.getText().lastIndexOf("!") + 1));
        if (isChild) {
            askAge(message);
        } else {
            sendChooseScheduleMessage(chatId);
        }
    }

    private void askAge(Message message) {
        sender.sendMessage(message.getChatId(), MessageConstants.HOW_OLD_ARE_YOU_MESSAGE);
    }

    @Override
    public void processNoActivityButton(Message message) {
        long chatId = message.getChatId();
        sender.sendMessage(chatId, MessageConstants.THAN_ACTIVITY_SCHEDULE_MESSAGE);
        showGamesTxtSchedule(chatId);
    }

    private void showGamesTxtSchedule(long chatId) {
        List<FestActivity> games = scheduleManager.getFestGameSchedule();
        if (games == null || games.isEmpty()) {
            logger.debug("No games found in schedule!");
            return;
        }
        int age = dbService.getAge(chatId);
        List<FestActivity> activities = games
                .stream()
                .filter(a -> age >= a.getStartAge() && age <= a.getEndAge())
                .collect(Collectors.toList());
        if (activities == null) {
            sender.sendMessage(chatId, MessageConstants.NO_MORE_ACTIVITIES_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(MessageConstants.ACTIVITIES_TIME_MESSAGE);
        for (FestActivity activity : activities) {
            sb.append(String.format("%s. \nМесто: %s\n\n", activity.getName(), activity.getRoom()));
        }
        sender.sendMessage(chatId, sb.toString());
        sendChooseScheduleMessage(chatId);
    }

    @Override
    public void processCancelButton(Message message) {
        long chatId = message.getChatId();
        dbService.setUserAction(chatId, UserActionType.SCHEDULE_ACCESS);
        //todo
        sender.editMessage(message, message.getText().substring(0, message.getText().indexOf("...") + 1));
        sendChooseScheduleMessage(chatId);
    }

    @Override
    public void processCoursesButton(Message message, String data) {
        int courseId = Integer.parseInt(data.replace(ButtonConstants.COURSE_BUTTON + ":", ""));
        FestCourse course = scheduleManager
                .getFestCourses()
                .stream()
                .filter(c -> c.getId() == courseId)
                .findFirst()
                .orElse(null);
        sender.sendMessage(message.getChatId(), course.getName() + "\n\n" + course.getDescr());
    }

    @Override
    public void processActivityButton(Message message, String data) {
        int activityId = Integer.parseInt(data.replace(ButtonConstants.ACTIVITY_BUTTON, ""));
        FestActivity activity = FestBotUtils.getActivityById(scheduleManager, activityId);
        if (activity == null) {
            return;
        }
        String messageText = ActivityType.GAME.equals(activity.getType()) ?
                String.format(GAME_DESCRIPTION__MESSAGE, activity.getName(), activity.getRoom()) :
                String.format(LECTURE_DESCRIPTION_MESSAGE, timeFormatter.format(activity.getStartTime()),
                        activity.getRoom(), activity.getName());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText(MessageConstants.YES_ANSWER);
        yesButton.setCallbackData(ButtonConstants.YES_BUTTON + ":" + activityId);
        buttons.add(yesButton);
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText(MessageConstants.NO_ANSWER);
        noButton.setCallbackData(ButtonConstants.NO_BUTTON);
        buttons.add(noButton);

        allButtons.add(buttons);
        markup.setKeyboard(allButtons);

        sender.sendMessage(message.getChatId(), messageText, markup);
    }

}
