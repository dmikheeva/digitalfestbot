package ru.botik.handlers;

import com.vdurmont.emoji.EmojiParser;

/**
 * Created by Daria on 13.04.2018.
 */
public final class MessageConstants {
    private MessageConstants() {

    }

    public static final String HELLO_MESSAGE = "Привет%s!" + "\n\n Я - @%s" + EmojiParser.parseToUnicode(":sunglasses:") +
            " Сегодня я помогу тебе не пропустить интересные лекции и активности!" +
            " Посещай их, а я буду спрашивать тебя кодовые слова, за" +
            " которые будут начислены очки. Очки затем можно обменять на подарки" +
            EmojiParser.parseToUnicode(":gift:") + " на ресепшен " + EmojiParser.parseToUnicode(":wink:") +
            "!\n\n Для начала выбери категорию:";
    public static final String HOW_OLD_ARE_YOU_MESSAGE = "Сколько тебе лет? Введи число от 8 до 17";
    public static final String WRONG_AGE_MESSAGE = "Неверный формат возраста. Введи число от 8 до 17.";

    public static final String CHOOSE_SCHEDULE_MESSAGE = "Выбери интересующую тебя категорию:";
    public static final String CHOOSE_LECTURE_MESSAGE = "Выбери интересующую тебя лекцию:";
    public static final String CHOOSE_GAME_MESSAGE = "Выбери интересующую тебя активность:";
    public static final String CHOOSE_LECTURE_AFTER_CORRECT_WORD_MESSAGE = "Давай выберем, куда пойдём дальше:";
    public static final String GAME_DESCRIPTION__MESSAGE = "%s\nМесто: %s.\nВы пойдёте?";
    public static final String LECTURE_DESCRIPTION_MESSAGE = "В %s начнётся лекция в кабинете %s на тему '%s'. Вы пойдёте?";

    public static final String CORRECT_ANSWER_MESSAGE = "Правильно!" +
            EmojiParser.parseToUnicode(":+1:") + " Количество набранных очков: %s";
    public static final String ALREADY_CHECKED_WORD_MESSAGE = "Ты уже угадал секретное слово!";
    public static final String ENTER_CHECK_WORD_MESSAGE = "Введите кодовое слово:";
    public static final String CHECK_SCORE_MESSAGE = "Количество набранных очков: %d\n";
    public static final String GET_PRESENT_MESSAGE = "Ты можешь показать это сообщение на ресепшен " +
            "и получить подарок" + EmojiParser.parseToUnicode(":gift:");
    public static final String GET_POINTS_MESSAGE = "Ты можешь набирать очки, посещая лекции и активности!" +
            EmojiParser.parseToUnicode(":wink:");

    public static final String NO_MORE_LECTURES_MESSAGE = "К сожалению, сегодня для тебя лекции закончились" +
            EmojiParser.parseToUnicode(":sob:");
    public static final String NO_MORE_ACTIVITIES_MESSAGE = "К сожалению больше нет запланированных активностей" +
            EmojiParser.parseToUnicode(":sad:");
    public static final String THAN_ACTIVITY_SCHEDULE_MESSAGE = "Тогда посмотри расписание активностей:";

    public static final String ACTIVITIES_TIME_MESSAGE = "Все активности проходят с 11:00 до 18:00.\n\n";

    public static final String YES_ANSWER = EmojiParser.parseToUnicode(":white_check_mark: Да");
    public static final String NO_ANSWER = EmojiParser.parseToUnicode(":red_circle: Нет");
    public static final String LECTURE_CATEGORY = EmojiParser.parseToUnicode(":pencil2: Лекции");
    public static final String OTHER_CATEGORY = EmojiParser.parseToUnicode(":video_game: Активности");
    public static final String MY_POINTS = EmojiParser.parseToUnicode(":eyeglasses: Мои очки");
    public static final String CANCEL_ANSWER = EmojiParser.parseToUnicode(":x: Отмена");

    public static final String UPDATE_SCHEDULE_ERROR = "Упссс! Произошла ошибка при обновлении расписания. Обратитесь к разработчику!";
    public static final String WRONG_CHECK_WORD_MESSAGE = "Упс! Неправильно! Попробуй еще раз... " +
            "Или нажми Отмена для выхода из режима ввода кодового слова.";

    public static final String END_FEST_MESSAGE = "Фестиваль подошёл к концу. До встречи в следующем году!";
    public static final String WAIT_YOU_LATER_MESSAGE = "Ждём тебя на наших курсах: \n";
    public static final String CONTACTS_MESSAGE = "Наши контакты: \n" + "www.codabra.org\n" + "Телефон: 8(800)222 34 07";
    public static final String GET_DISCOUNT_MESSAGE = "Ты можешь записаться на курсы по ссылке\n%s\n и получить скидку!" +
            EmojiParser.parseToUnicode(":moneybag:");
    public static final String SEE_YOU_LATER_MESSAGE = "Надеемся увидеть тебя снова!";


    public static final String START_MESSAGE = "/start";
    public static final String UPDATE_SCHEDULE_MESSAGE = "/updateschedule";
    public static final String KEYBOARD_MESSAGE = "/keyboard";

}
