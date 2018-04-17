package ru.botik.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

/**
 *
 * В 20-00 всем участникам фестиваля должно быть отправлено сообщение с опросом о том, понравился ли фестиваль
 * и предложение посетить курсы
 *
 */
public class OfferCoursesTimer {
    private static final Logger logger = LogManager.getLogger(OfferCoursesTimer.class.getName());

    private OfferCoursesTimerTask timerTask;
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public OfferCoursesTimer(OfferCoursesTimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public void start() {
        //todo вынести задание даты
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        (new Timer()).schedule(timerTask, calendar.getTime());
        if (logger.isDebugEnabled()) {
            logger.debug("Timer task started successfully. Will be performed at " +
                    timeFormatter.format(calendar.getTime()));
        }
    }

}