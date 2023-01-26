import java.time.LocalDate;
import java.time.LocalTime;

public record Booking(Room room, LocalDate date, LocalTime time, int duration) {
    public boolean confirm() {
        return room.addBooking(this);
    }

    public void cancel() {
        room.cancelBooking(this);
    }

    @Override
    public String toString() {
        return room.getRoomNumber() + " on " + date + " at " + time + " for " + (duration + 1) + " minutes.\n\n";
    }

}
