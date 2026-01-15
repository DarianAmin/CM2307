package studentrentals.repo;

import studentrentals.model.Booking;
import java.time.LocalDate;
import java.util.*;

// Stores and manages bookings in memory
public class BookingRepository {

    // Internal list of all bookings
    private final List<Booking> bookings = new ArrayList<>();

    // Generates unique booking IDs
    private final IdGenerator ids = new IdGenerator();

    // Creates and saves a new booking
    public Booking create(String studentId, String roomId,
                          LocalDate start, LocalDate end) {

        // Build booking with generated ID
        Booking b = new Booking(ids.nextId("BOOK"), studentId, roomId, start, end);

        // Store booking
        bookings.add(b);

        return b;
    }

    // Finds a booking by its unique ID
    public Optional<Booking> findById(String bookingId) {
        return bookings.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();
    }

    // Returns all bookings made by a given student
    public List<Booking> findByStudentId(String studentId) {
        List<Booking> out = new ArrayList<>();
        for (Booking b : bookings)
            if (b.getStudentId().equals(studentId)) out.add(b);
        return out;
    }

    // Returns all bookings for a given room
    public List<Booking> findByRoomId(String roomId) {
        List<Booking> out = new ArrayList<>();
        for (Booking b : bookings)
            if (b.getRoomId().equals(roomId)) out.add(b);
        return out;
    }

    // Returns a safe copy of all bookings
    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }
}
