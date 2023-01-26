import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BookingBot extends TelegramLongPollingBot {
    private final static String BOT_TOKEN = "5804386323:AAHvgmcONTK6IlgrTQAri595leZTSQsG1vw";
    private final static String BOT_USER_NAME = "Booking_SDU";
    private final Map<Long, List<Booking>> bookings = new HashMap<>();
    private final Map<Long, State> userState = new HashMap<>();
    private enum State {INIT, BOOK_STEP_0, BOOK_STEP_1, MY_BOOKS, CANCEL}
    private final Data data = new Data();
    private List<Room> rooms;
    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery().getMessage().getChat().getFirstName() + " " + update.getCallbackQuery().getData());

            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = update.getCallbackQuery().getMessage();
            String callbackData = callbackQuery.getData();

            long chatId = callbackQuery.getMessage().getChatId();

            message.setReplyMarkup(null);

            if (callbackData.equals("practice_room")) {
                rooms = data.getRooms();
                sendMsg(message, "Please enter the practice room number and the date (YYYY-MM-DD) and time (HH:MM) of your desired appointment:");
                userState.put(chatId, State.BOOK_STEP_1);
            } else if (callbackData.equals("lecture_hall")) {
                rooms = data.getLectureHall();
                sendMsg(message, "Please enter the lecture hall number and the date (YYYY-MM-DD) and time (HH:MM) of your desired appointment:");
                userState.put(chatId, State.BOOK_STEP_1);
            } else if (callbackData.equals("mini_red_hall")) {
                rooms = List.of(data.getMiniRedHall());
                sendMsg(message, "Please enter the date (YYYY-MM-DD) and time (HH:MM) of your desired appointment:");
                userState.put(chatId, State.BOOK_STEP_1);
            }else if(bookings.containsKey(chatId)){
                for (Booking booking : bookings.get(chatId)) {
                   if(callbackData.equals(String.valueOf(booking))){
                       if(bookings.get(chatId).size() == 1)bookings.remove(chatId);
                       else bookings.get(chatId).remove(booking);
                       booking.cancel();
                       sendMsg(message,booking + "canceled successfully");
                       userState.put(chatId,State.INIT);
                   }
                }
            }

        } else if (update.hasMessage() && update.getMessage().hasText()) {

            System.out.println(update.getMessage().getChat().getFirstName() + " " + update.getMessage().getText());
            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (!userState.containsKey(chatId)) {
                userState.put(chatId, BookingBot.State.INIT);
            }

            State currentState = userState.get(chatId);
            switch (currentState) {

                case INIT:
                    if (message.getText().equals("/start")) {
                        sendMsg(message, """
                                Welcome to MyBot! I am here to assist you with all your room booking needs. Whether you're looking to book a room, cancel a booking or check your upcoming reservations, I am here to help.
                                
                                Here are a list of commands you can use:

                                /book - book a room
                                /cancel - cancel a booked room
                                /myBooks - view your booked rooms
                                /search - search for available rooms based on your preferences
                                /help - view a list of all commands and their functions

                                Type any of these commands to get started!""");
                    } else if (message.getText().equals("/book")) {

                        userState.put(chatId, State.BOOK_STEP_0);
                        onUpdateReceived(update);

                    } else if (message.getText().equals("/cancel")) {

                        userState.put(chatId, State.CANCEL);
                        onUpdateReceived(update);

                    } else if (message.getText().equals("/mybooks")) {

                        userState.put(chatId, State.MY_BOOKS);
                        onUpdateReceived(update);

                    }
                    break;
                case BOOK_STEP_0:

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    List<InlineKeyboardButton> firstRow = new ArrayList<>();
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

                    inlineKeyboardButton.setText("Practice Room");
                    inlineKeyboardButton.setCallbackData("practice_room");
                    firstRow.add(inlineKeyboardButton);
                    buttons.add(firstRow);

                    inlineKeyboardButton = new InlineKeyboardButton();
                    firstRow = new ArrayList<>();
                    inlineKeyboardButton.setText("Lecture Hall");
                    inlineKeyboardButton.setCallbackData("lecture_hall");
                    firstRow.add(inlineKeyboardButton);
                    buttons.add(firstRow);

                    inlineKeyboardButton = new InlineKeyboardButton();
                    firstRow = new ArrayList<>();
                    inlineKeyboardButton.setText("Mini Red Hall");
                    inlineKeyboardButton.setCallbackData("mini_red_hall");
                    firstRow.add(inlineKeyboardButton);
                    buttons.add(firstRow);

                    markup.setKeyboard(buttons);
                    message.setReplyMarkup(markup);
                    sendMsg(message, "Please select the type of the room:");
                    break;

                case BOOK_STEP_1:
                    String[] input;
                    if(rooms.size() == 1)input = ("MiniRedHall " + message.getText()).split(" ");
                    else input = message.getText().split(" ");

                    if (input.length == 4) {
                        Room room = getRoomByNumber(input[0]);
                        if (room == null) {
                            sendMsg(message, "Invalid room number. Please enter a valid room number.");
                            return;
                        }
                        LocalDate date;
                        LocalTime time;
                        try {
                            date = LocalDate.parse(input[1]);
                            time = LocalTime.parse(input[2]);
                        } catch (DateTimeParseException e) {
                            sendMsg(message, "Invalid input. Please enter a date and time in the format YYYY-MM-DD HH:MM");
                            return;
                        }
                        int duration = Integer.parseInt(input[3]);

                        if (room.isOccupied(date, time, duration-1)) {
                            sendMsg(message, "Sorry, the room is already booked at that time. Please choose another time.");
                            userState.put(chatId,State.INIT);
                            return;
                        }

                        Booking booking = new Booking(room, date, time, Integer.parseInt(input[3])-1);
                        if (!booking.confirm()) {
                            sendMsg(message, "Sorry, there was an error confirming your booking. Please try again later.");
                            return;
                        }

                        if (!bookings.containsKey(chatId)) {
                            bookings.put(chatId, new ArrayList<>());
                        }
                        bookings.get(chatId).add(booking);
                        userState.put(chatId, State.INIT);
                        sendMsg(message, "Your booking has been confirmed for room " + room.getRoomNumber() + " on " + date + " at " + time + " for "+duration+" minutes. Thank you!");
                    } else {
                        if(rooms.size() == 1)sendMsg(message, "Invalid input. Please enter a date, time in the format YYYY-MM-DD HH:MM");
                        else sendMsg(message, "Invalid input. Please enter a room number, date, and time in the format roomNumber YYYY-MM-DD HH:MM");
                    }
                    break;

                case CANCEL:
                    if (bookings.containsKey(chatId)) {

                        InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons1 = new ArrayList<>();
                        List<InlineKeyboardButton> firstRow1 ;
                        InlineKeyboardButton inlineKeyboardButton1 ;

                        for (Booking booking : bookings.get(chatId)) {
                            firstRow1 = new ArrayList<>();
                            inlineKeyboardButton1 = new InlineKeyboardButton();
                            inlineKeyboardButton1.setText(String.valueOf(booking));
                            inlineKeyboardButton1.setCallbackData(String.valueOf(booking));
                            firstRow1.add(inlineKeyboardButton1);
                            buttons1.add(firstRow1);
                        }

                        markup1.setKeyboard(buttons1);
                        message.setReplyMarkup(markup1);
                        sendMsg(message, "Select the room for canceling:");
                    } else {
                        sendMsg(message, "You do not have any previously booked services.");
                    }
                    userState.put(chatId, State.INIT);
                    break;

                case MY_BOOKS:
                    if (bookings.containsKey(chatId)) {
                        sendMsg(message, "Your Booking: \n\n" + bookings.get(chatId).toString().replace(", ", ""));
                    } else {
                        sendMsg(message, "You do not have any previously booked services.");
                    }
                    userState.put(chatId, State.INIT);
                    break;

                default:
                    sendMsg(message, "Invalid command. Please type /book to book a room.");

            }
        }
    }

    private Room getRoomByNumber(String number) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equals(number)) {
                return room;
            }
        }
        return null;
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(message.getReplyMarkup());
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}