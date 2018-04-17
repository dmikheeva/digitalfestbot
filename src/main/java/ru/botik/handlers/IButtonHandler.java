package ru.botik.handlers;

import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Daria on 16.04.2018.
 */
public interface IButtonHandler {
    void processLikeEventButton(Message message);

    void processYesButton(Message message, String data);

    void processNoButton(Message message);

    void processDislikeEventButton(Message message);

    void processParentChildButton(Message message, String data);

    void processActivityButton(Message message, String data);

    void processNoActivityButton(Message message);

    void processCancelButton(Message message);

    void processCoursesButton(Message message, String data);
}
