package ru.botik.handlers;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;

/**
 * Created by Daria on 16.04.2018.
 */
public interface IMessageSender {
    void sendMessage(long chatId, String messageText);

    void sendMessage(long chatId, String messageText, ReplyKeyboard replyKeyboard);

    void editMessage(Message message, String newText);

    void sendSticker(long chatId, String stickerName);
}
