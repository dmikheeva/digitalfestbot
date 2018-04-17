package ru.botik.handlers;

import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Daria on 16.04.2018.
 */
public interface IMessageHandler {
    void processStartMessage(Message message, String botName);

    void processKeyboardMessage(Message message);

    void processScheduleAccessMessage(Message message);

    void processWordGuessingMessage(Message message);

    void processAgeInputMessage(Message message);

    void processFestEndingMessage(Message message);
}
