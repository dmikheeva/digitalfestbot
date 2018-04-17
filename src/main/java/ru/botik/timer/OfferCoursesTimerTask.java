package ru.botik.timer;

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.botik.db.DBService;
import ru.botik.db.IDBService;
import ru.botik.handlers.IMessageSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Daria on 12.04.2018.
 */
public class OfferCoursesTimerTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger(OfferCoursesTimerTask.class.getName());

    private static final String DO_YOU_LIKE_FEST_MESSAGE = "Наш фестиваль подошёл к концу. Тебе понравилось?";
    private static final String YES_ANSWER_NAME = EmojiParser.parseToUnicode(":white_check_mark: Да");
    private static final String NO_ANSWER_NAME = EmojiParser.parseToUnicode(":red_circle: Нет");

    private IDBService dbService;
    private IMessageSender sender;

    public OfferCoursesTimerTask(IDBService dbService, IMessageSender messageSender) {
        this.dbService = dbService;
        this.sender = messageSender;
    }

    @Override
    public void run() {
        logger.debug("Start sending messages...");

        List<Long> chatIds = dbService.getChatIds();
        if (chatIds == null) {
            return;
        }
        InlineKeyboardMarkup markup = createFinalMessageMarkup();
        for (int i = 0; i < chatIds.size(); i++) {
            logger.debug("Sending messages... Iteration: " + (i + 1));
            if (i % 30 == 0) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            sender.sendMessage(chatIds.get(i), DO_YOU_LIKE_FEST_MESSAGE, markup);
        }
        logger.debug("All " + chatIds.size() + " messages sent successfully...");
    }

    private InlineKeyboardMarkup createFinalMessageMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText(YES_ANSWER_NAME);
        yesButton.setCallbackData("yesButton:like");
        buttons.add(yesButton);
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText(NO_ANSWER_NAME);
        noButton.setCallbackData("noButton:dislike");
        buttons.add(noButton);
        allButtons.add(buttons);
        markup.setKeyboard(allButtons);
        return markup;
    }
}