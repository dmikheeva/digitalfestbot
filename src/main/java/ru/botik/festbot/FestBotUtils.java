package ru.botik.festbot;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.botik.model.FestActivity;
import ru.botik.schedule.IScheduleService;

import java.util.ArrayList;
import java.util.List;

import static ru.botik.handlers.MessageConstants.*;

/**
 * Created by Daria on 16.04.2018.
 */
public class FestBotUtils {

    public static ReplyKeyboardMarkup createChooseEventKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(LECTURE_CATEGORY);
        row.add(OTHER_CATEGORY);
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(MY_POINTS);
        keyboard.add(row);
        markup.setKeyboard(keyboard);

        return markup;
    }

    public static FestActivity getActivityById(IScheduleService scheduleManager, int activityId) {
        List<FestActivity> suitableActivities = (activityId < 1000) ?
                scheduleManager.getFestLecturesSchedule() :
                scheduleManager.getFestGameSchedule();
        return suitableActivities
                .stream()
                .filter(a -> a.getId() == activityId)
                .findFirst()
                .orElse(null);
    }
}
