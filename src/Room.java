import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomNumber;
    private final List<Booking> bookings;
    public Room(String roomNumber) {
        this.roomNumber = roomNumber;
        this.bookings = new ArrayList<>();
    }
    //Ormlite
    public String getRoomNumber() {
        return roomNumber;
    }
    public boolean isOccupied(LocalDate date, LocalTime startTime, int duration) {
        for (Booking booking : bookings) {
            LocalTime endTime = startTime.plusMinutes(duration);
            if (booking.date().equals(date) &&
                    !(startTime.isBefore(booking.time()) && endTime.isBefore(booking.time()) ||
                            startTime.isAfter(booking.time().plusMinutes(booking.duration())))) {
                return true;
            }
        }
        return false;
    }
    public boolean addBooking(Booking booking) {
        if (!isOccupied(booking.date(), booking.time(), booking.duration())) {
            bookings.add(booking);
            return true;
        }
        return false;
    }
    public void cancelBooking(Booking booking) {
        bookings.remove(booking);
    }
}