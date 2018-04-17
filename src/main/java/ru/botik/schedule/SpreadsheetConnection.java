package ru.botik.schedule; /**
 * Created by Daria on 07.11.2017.
 */

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsRequestInitializer;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SpreadsheetConnection {
    private static final Logger logger = LogManager.getLogger(SpreadsheetConnection.class.getName());

    private static final String APPLICATION_NAME = "Dasha Quickstart";//todo
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private String keyFileName;
    private String spreadsheetId;
    private Sheets sheetsService;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            //System.exit(1);
        }
    }

    public SpreadsheetConnection(String spreadsheetId, String keyFileName) {
        this.spreadsheetId = spreadsheetId;
        this.keyFileName = keyFileName;
        try {
            sheetsService = getSheetsService();
        } catch (IOException | URISyntaxException e) {
            logger.error(e);
        }
    }

    /**
     * Build and return an authorized Sheets API client service.
     *
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public Sheets getSheetsService() throws IOException, URISyntaxException {
        byte[] bytes = Files.readAllBytes(Paths.get("conf" + File.separator + keyFileName));
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .setSheetsRequestInitializer(new SheetsRequestInitializer(new String(bytes)))
                .build();
    }

    //todo
    public ValueRange getDataRange(String range) throws IOException {
        return sheetsService
                .spreadsheets()
                .values()
                .get(spreadsheetId, range)
                .execute();
    }

}
