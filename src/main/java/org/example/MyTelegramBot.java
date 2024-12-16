package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {

    private enum BotState {
        DEFAULT,
        ADDING_TEACHER_FIRSTNAME,
        ADDING_TEACHER_LASTNAME,
        ADDING_TEACHER_FATHERNAME
    }

    private BotState currentState = BotState.DEFAULT;
    private String tempFirstName;
    private String tempLastName;
    private String tempFatherName;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (currentState) {
                case DEFAULT:
                    if (messageText.equals("/start")) {
                        sendWelcomeMessage(chatId);
                    } else if (messageText.equals("/classtime")) {
                        String paratimeInfo = getParatimeInfo();
                        sendMessage(chatId, paratimeInfo);
                    } else if (messageText.equals("/addteacher")) {
                        sendMessage(chatId, "Введите имя преподавателя:");
                        currentState = BotState.ADDING_TEACHER_FIRSTNAME;
                    } else {
                        sendMessage(chatId, "You said: " + messageText);
                    }
                    break;
                case ADDING_TEACHER_FIRSTNAME:
                    tempFirstName = messageText;
                    sendMessage(chatId, "Введите фамилию преподавателя:");
                    currentState = BotState.ADDING_TEACHER_LASTNAME;
                    break;
                case ADDING_TEACHER_LASTNAME:
                    tempLastName = messageText;
                    sendMessage(chatId, "Введите отчество преподавателя:");
                    currentState = BotState.ADDING_TEACHER_FATHERNAME;
                    break;
                case ADDING_TEACHER_FATHERNAME:
                    tempFatherName = messageText;
                    addTeacherToDatabase(tempFirstName, tempLastName, tempFatherName);
                    sendMessage(chatId, "Преподаватель успешно добавлен");
                    currentState = BotState.DEFAULT;
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("get_paratime")) {
                String paratimeInfo = getParatimeInfo();
                sendMessage(chatId, paratimeInfo);
            }
        }
    }

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Welcome! Click the button below to get information about paratimes.");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        // Кнопка для получения информации о парах
        InlineKeyboardButton paratimeButton = new InlineKeyboardButton();
        paratimeButton.setText("Get Paratime Info");
        paratimeButton.setCallbackData("get_paratime");
        rowInline.add(paratimeButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendStartCommand(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please use the /start command.");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/start"));

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@MyOwnTimeTable_bot";
    }

    @Override
    public String getBotToken() {
        return "7321581027:AAEV8I-TZBiAnGCm1AZyfWEtq7SYwu9GRNo";
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message); // Отправка сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void addTeacherToDatabase(String firstName, String lastName, String fatherName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Teacher teacher = new Teacher();
            teacher.setFirstname(firstName);
            teacher.setLastname(lastName);
            teacher.setFathername(fatherName);
            session.save(teacher);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private String getParatimeInfo() {
        StringBuilder info = new StringBuilder();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Query<Paratime> query = session.createQuery("FROM Paratime", Paratime.class);
            List<Paratime> paratimes = query.list();
            transaction.commit();
            info.append("Расписание пар\n");
            for (Paratime paratime : paratimes) {
                info.append(paratime.getNumber()).append(". ");
                info.append(paratime.getStringStarttime()).append(" - ");
                info.append(paratime.getStringEndtime()).append("\n");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        //HibernateUtil.shutdown();
        return info.toString();
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
