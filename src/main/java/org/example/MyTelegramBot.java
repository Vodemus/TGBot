package org.example;

import model.*;
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

import java.util.ArrayList;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {

    private enum BotState {
        DEFAULT,
        ADDING_TEACHER,
        ADDING_SUBJECT,
        ADDING_PARAROOM,
        ADDING_SCHEDULE
    }

    private final DatabaseService DB = new DatabaseService();
    private BotState currentState = BotState.DEFAULT;
    private String tempFirstName;
    private String tempLastName;
    private String tempFatherName;

    private int selectedDisciplineID;
    private int selectedParatypeID;
    private int selectedPararoomID;
    private int selectedTeacherID;
    private int selectedWeekdayID;
    private int selectedParatimeID;


    @Override
    public String getBotUsername() {
        return "@MyOwnTimeTable_bot";
    }

    @Override
    public String getBotToken() {
        return "7321581027:AAEV8I-TZBiAnGCm1AZyfWEtq7SYwu9GRNo";
    }
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
                        String paratimeInfo = DB.getParatimeInfo();
                        sendMessage(chatId, paratimeInfo);
                    } else if (messageText.equals("/addteacher")) {
                        sendMessage(chatId, "Введите имя преподавателя:");
                        currentState = BotState.ADDING_TEACHER;
                    } else if (messageText.equals("/addsubject")) {
                        sendMessage(chatId, "Введите название предмета:");
                        currentState = BotState.ADDING_SUBJECT;
                    } else if (messageText.equals("/addpararoom")) {
                        sendMessage(chatId, "Введите номер аудитории:");
                        currentState = BotState.ADDING_PARAROOM;
                    } else if (messageText.equals("/addschedule")) {
                        sendMessage(chatId, "Добавить предмет расписания:");
                        sendChooseSubjectMessage(chatId, DB.getSubjects());
                        currentState = BotState.ADDING_SCHEDULE;
                    } else if (messageText.equals("/getsubjects")) {
                        sendChooseSubjectMessage(chatId, DB.getSubjects());
                    } else if (messageText.equals("/getteachers")) {
                        sendChooseTeacherMessage(chatId, DB.getTeachers());
                    } else if (messageText.equals("/getpararooms")) {
                        sendChoosePararoomMessage(chatId, DB.getPararooms());
                    }else if (messageText.equals("/getparatypes")) {
                        sendChooseParatypeMessage(chatId, DB.getParatype());
                    }else {
                        sendMessage(chatId, "You said: " + messageText);
                    }
                    break;
                case ADDING_TEACHER:
                    handleTeacherAddition(chatId, messageText);
                    break;
                case ADDING_SUBJECT:
                    handleSubjectAddition(chatId, messageText);
                    break;
                case ADDING_PARAROOM:
                    handlePararoomAddition(chatId, messageText);
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            String model = callbackData.split("_")[0];
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (currentState == BotState.ADDING_SCHEDULE){
                switch (model){
                    case "subject":
                        selectedDisciplineID = Integer.parseInt(callbackData.split("_")[1]);
                        sendChooseParatypeMessage(chatId, DB.getParatype());
                        break;
                    case "paratype":
                        selectedParatypeID = Integer.parseInt(callbackData.split("_")[1]);
                        sendChoosePararoomMessage(chatId, DB.getPararooms());
                        break;
                    case "pararoom":
                        selectedPararoomID = Integer.parseInt(callbackData.split("_")[1]);
                        sendChooseTeacherMessage(chatId, DB.getTeachers());
                        break;
                    case "teacher":
                        selectedTeacherID = Integer.parseInt(callbackData.split("_")[1]);
                        sendChooseWeekdayMessage(chatId);
                        break;
                    case "weekday":
                        selectedWeekdayID = Integer.parseInt(callbackData.split("_")[1]);
                        sendChoosePararoomMessage(chatId, DB.getPararooms());
                        break;
                }
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
    private void sendChooseSubjectMessage(long chatId, List<Discipline> subjects) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please select a subject:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Discipline subject : subjects) {
            InlineKeyboardButton subjectButton = new InlineKeyboardButton();
            subjectButton.setText(subject.getName());
            subjectButton.setCallbackData("subject_" + subject.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(subjectButton);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendChoosePararoomMessage(long chatId, List<Pararoom> pararooms) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please select a room:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Pararoom pararoom : pararooms) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(pararoom.getName());
            button.setCallbackData("pararoom_" + pararoom.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendChooseParatypeMessage(long chatId, List<Paratype> paratypes) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please select a type:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Paratype paratype : paratypes) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(paratype.gettype());
            button.setCallbackData("paratype_" + paratype.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendChooseTeacherMessage(long chatId, List<Teacher> teachers) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please select a professor:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Teacher teacher : teachers) {
            InlineKeyboardButton teacherButton = new InlineKeyboardButton();
            teacherButton.setText(teacher.toString());
            teacherButton.setCallbackData("teacher_" + teacher.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(teacherButton);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendChooseWeekdayMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выбери день недели:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        String[] weekdays = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

        for (int i = 0; i < weekdays.length; i++) {
            InlineKeyboardButton weekdayButton = new InlineKeyboardButton();
            weekdayButton.setText(weekdays[i]);
            weekdayButton.setCallbackData("weekday_" + (i + 1)); // Assuming weekday IDs start from 1
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(weekdayButton);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendChooseWeekparityMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выбери неделю :");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        String[] weekparities = {"Верхняя", "Нижняя", "Каждая"};

        for (int i = 0; i < weekparities.length; i++) {
            InlineKeyboardButton weekparityButton = new InlineKeyboardButton();
            weekparityButton.setText(weekparities[i]);
            weekparityButton.setCallbackData("weekparity_" + (i + 1));
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(weekparityButton);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
    private void handleTeacherAddition(long chatId, String messageText) {
        if (tempFirstName == null) {
            tempFirstName = messageText;
            sendMessage(chatId, "Введите фамилию преподавателя:");
        } else if (tempLastName == null) {
            tempLastName = messageText;
            sendMessage(chatId, "Введите отчество преподавателя:");
        } else { tempFatherName = messageText;
            DB.addTeacherToDatabase(tempFirstName, tempLastName, tempFatherName);
            sendMessage(chatId, "Преподаватель успешно добавлен");
            currentState = BotState.DEFAULT;
            tempFirstName = null;
            tempLastName = null;
            tempFatherName = null;
        }
    }
    private void handleSubjectAddition(long chatId, String messageText) {
        String SubjectName = messageText;
        Discipline discipline = new Discipline(SubjectName);
        DB.addSubjectToDatabase(discipline);
        sendMessage(chatId, "Предмет успешно добавлен");
        currentState = BotState.DEFAULT;
    }
    private void handlePararoomAddition(long chatId, String messageText) {
        String PararoomName = messageText;
        Pararoom pararoom = new Pararoom(PararoomName);
        DB.addPararoomToDatabase(pararoom);
        sendMessage(chatId, "Аудитория успешно добавлена");
        currentState = BotState.DEFAULT;
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
