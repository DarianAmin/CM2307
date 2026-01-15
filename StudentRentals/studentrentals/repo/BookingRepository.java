package studentrentals.repo;

import studentrentals.model.Booking;
import java.time.LocalDate;
import java.util.*;

public class BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();
    private final IdGenerator ids = new IdGenerator();

    public Booking create(String studentId, String roomId, LocalDate start, LocalDate end) {
        Booking b = new Booking(ids.nextId("BOOK"), studentId, roomId, start, end);
        bookings.add(b);
        return b;
    }

    public Optional<Booking> findById(String bookingId) {
        return bookings.stream().filter(b -> b.getId().equals(bookingId)).findFirst();
    }

    public List<Booking> findByStudentId(String studentId) {
        List<Booking> out = new ArrayList<>();
        for (Booking b : bookings) if (b.getStudentId().equals(studentId)) out.add(b);
        return out;
    }

    public List<Booking> findByRoomId(String roomId) {
        List<Booking> out = new ArrayList<>();
        for (Booking b : bookings) if (b.getRoomId().equals(roomId)) out.add(b);
        return out;
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }   
}