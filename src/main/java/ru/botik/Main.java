package ru.botik;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.botik.db.DbPoolConnection;
import ru.botik.festbot.DigitalFestBot;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Daria on 07.11.2017.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            Properties properties = new Properties();
            String driverName = "", host = "", port = "", dbName = "", userName = "", password = "";
            String botToken = "", botUserName = "";
            try (FileInputStream in = new FileInputStream("src/main/resources/config.properties")) {
                properties.load(in);
                driverName = properties.getProperty("db.driverClassName");
                host = properties.getProperty("db.host");
                port = properties.getProperty("db.port");
                dbName = properties.getProperty("db.name");
                userName = properties.getProperty("db.userName");
                password = properties.getProperty("db.password");
                botToken = properties.getProperty("bot.token");
                botUserName = properties.getProperty("bot.userName");
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Init db connection...");
            new DbPoolConnection(driverName, host, port, dbName, userName, password);
            logger.info("Db connection acquired!");

            logger.info("Init bot...");
            botsApi.registerBot(new DigitalFestBot(botToken, botUserName));
            logger.info("Bot registered.");
        } catch (TelegramApiException | URISyntaxException | SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

