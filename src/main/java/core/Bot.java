package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.InstrumentChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * Created by OlegK on 7/28/2018.
 */
public class Bot extends TelegramLongPollingBot {

    private final String GRN_MESSAGE = " Грн";
    private static final String FIRST_MESSAGE = "Вітаю! Бот допоможе Вам зконвертувати обрану валюту у гривні. Оберіть потрібну валюту: ";
    private static final String ENTER_AMOUNT_MESSAGE = "Тепер введіть потрібну суму: ";
    private static final String AMOUNT_IN_UAH_MESSAGE = " складає приблизно: \n";

    private static java.net.URL URL;
    private static HashMap<Long, String> selectedCurrencyForUser = new HashMap<>();

    public String getBotUsername() {
        return "kolom_test_bot";
        //возвращаем юзера
    }

    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage(); // Это нам понадобится
        Long userId = msg.getChatId();
        String inputMessage = msg.getText();
        boolean isNumeric = inputMessage.chars().allMatch(Character::isDigit);
        if (inputMessage.equals("/start")) {
            sendFirstMsg(msg, FIRST_MESSAGE);
        } else {

            if (!isNumeric) {
                saveInstruments(userId, inputMessage);
                sendAmountMessage(msg, ENTER_AMOUNT_MESSAGE);
            } else {
                Double result = convert(selectedCurrencyForUser.get(userId), inputMessage);
                String resultMessage =
                        Double.valueOf(inputMessage)
                        + " "
                        + selectedCurrencyForUser.get(userId)
                        + AMOUNT_IN_UAH_MESSAGE
                        + String.format("%.2f", result)
                        + GRN_MESSAGE;
                sendResultMessage(msg, resultMessage);
            }
        }
    }

    private void saveInstruments(Long userId, String inputMessage) {
        if (InstrumentChecker.isCurrencyAvailable(inputMessage)) {
            selectedCurrencyForUser.put(userId, inputMessage);
        }
    }

    private Double convert(String instrument, String inputMessage) {
        HashMap<String, Double> rates = null;
        try {
            File jsonFile = receiveJsonFile();
            rates = jsonToMap(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double inputAmount = Double.valueOf(inputMessage);
        if (rates != null) {
            Double rateAmount = rates.get(instrument) / 100;
            return inputAmount * rateAmount;
        } else {
            return null;
        }
    }

//    private String getInputInstrument(String inputMessage) {
//        String[] rateInfo = inputMessage.split(" ");
//        return rateInfo[0];
//    }
//
//    private Double getInputAmount(String inputMessage) {
//        String[] rateInfo = inputMessage.split(" ");
//        return Double.valueOf(rateInfo[1]);
//    }

    private File receiveJsonFile() throws IOException {
        URL = new URL("https://bank.gov.ua/NBU_Exchange/exchange?json");
        File jsonFile = new File("E:/botFile.json");
        FileUtils.copyURLToFile(URL, jsonFile);
        return jsonFile;
    }

    private HashMap<String, Double> jsonToMap(File jsonFile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        CurrencyExchangeRate[] ratesArray = mapper.readValue(new FileInputStream(jsonFile), CurrencyExchangeRate[].class);

        HashMap<String, Double> ratesMap = new HashMap<>();
        for (CurrencyExchangeRate rate : ratesArray) {
            ratesMap.put(rate.getInstrument(), rate.getAmount());
        }

        return ratesMap;
    }

    public String getBotToken() {
        return "606874676:AAEXX0wkFyumy8ipXQqDDevoUo9KUVJKbyY";
        //Токен бота
    }

    private void sendFirstMsg(Message msg, String text) {
        SendMessage message = new SendMessage();
        setButtons(message);
        message.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        message.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendResultMessage(Message msg, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        message.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAmountMessage(Message msg, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        message.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("USD"));
        keyboardFirstRow.add(new KeyboardButton("EUR"));
        keyboardFirstRow.add(new KeyboardButton("AUD"));
        keyboardFirstRow.add(new KeyboardButton("NZD"));
        keyboardFirstRow.add(new KeyboardButton("CAD"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("GBP"));
        keyboardSecondRow.add(new KeyboardButton("CHF"));
        keyboardSecondRow.add(new KeyboardButton("JPY"));
        keyboardSecondRow.add(new KeyboardButton("KZT"));
        keyboardSecondRow.add(new KeyboardButton("RUB"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
    }

}
