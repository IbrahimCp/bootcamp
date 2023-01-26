import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Data {
    private final List<Room> rooms = new ArrayList<>(Arrays.asList(
            new Room("F101"), new Room("F102"), new Room("F103"), new Room("F104"), new Room("F105"),
            new Room("F201"), new Room("F202"), new Room("F203"), new Room("F204"), new Room("F205"),
            new Room("F301"), new Room("F302"), new Room("F303"), new Room("F304"), new Room("F305"),
            new Room("F401"), new Room("F402"), new Room("F403"), new Room("F404"), new Room("F405"),
            new Room("H101"), new Room("H102"), new Room("H103"), new Room("H104"), new Room("H105"),
            new Room("H201"), new Room("H202"), new Room("H203"), new Room("H204"), new Room("H205"),
            new Room("H301"), new Room("H302"), new Room("H303"), new Room("H304"), new Room("H305"),
            new Room("H401"), new Room("H402"), new Room("H403"), new Room("H404"), new Room("H405"),
            new Room("G101"), new Room("G102"), new Room("G103"), new Room("G104"), new Room("G105"),
            new Room("G201"), new Room("G202"), new Room("G203"), new Room("G204"), new Room("G205"),
            new Room("G301"), new Room("G302"), new Room("G303"), new Room("G304"), new Room("G305"),
            new Room("G401"), new Room("G402"), new Room("G403"), new Room("G404"), new Room("G405"),
            new Room("D101"), new Room("D102"), new Room("D103"), new Room("D104"), new Room("D105"),
            new Room("D201"), new Room("D202"), new Room("D203"), new Room("D204"), new Room("D205"),
            new Room("D301"), new Room("D302"), new Room("D303"), new Room("D304"), new Room("D305"),
            new Room("D401"), new Room("D402"), new Room("D403"), new Room("D404"), new Room("D405"),
            new Room("E101"), new Room("E102"), new Room("E103"), new Room("E104"), new Room("E105"),
            new Room("E201"), new Room("E202"), new Room("E203"), new Room("E204"), new Room("E205"),
            new Room("E301"), new Room("E302"), new Room("E303"), new Room("E304"), new Room("E305"),
            new Room("E401"), new Room("E402"), new Room("E403"), new Room("E404"), new Room("E405")
    ));
    private final List<Room> lectureHall = new ArrayList<>(Arrays.asList(
            new Room("A1"), new Room("A2"),
            new Room("B1"), new Room("B2"),
            new Room("C1"), new Room("C2"),
            new Room("D1"), new Room("D2")
    ));

    private final Room miniRedHall = new Room("MiniRedHall");

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Room> getLectureHall() {
        return lectureHall;
    }

    public Room getMiniRedHall() {
        return miniRedHall;
    }

}