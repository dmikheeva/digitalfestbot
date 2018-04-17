package ru.botik.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Daria on 15.04.2018.
 */
public class MessageSender implements IMessageSender{
    private static final Logger logger = LogManager.getLogger(MessageSender.class.getName());
    TelegramLongPollingBot bot;

    public MessageSender(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void editMessage(Message message, String newText) {
        EditMessageText editMessageText = new EditMessageText().
                setChatId(message.getChatId()).
                setMessageId(message.getMessageId()).
                setText(newText);
        try {
            bot.execute(editMessageText);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendMessage(long chatId, String messageText) {
        sendMessage(chatId, messageText, null);
    }

    public void sendMessage(long chatId, String messageText, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setReplyMarkup(replyKeyboard);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendSticker(long chatId, String stickerName) {
        try {
            bot.sendSticker(new SendSticker().
                    setChatId(chatId).
                    setSticker(stickerName));
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }


}
