package studentrentals.service;

import studentrentals.model.Booking;
import studentrentals.model.BookingStatus;
import studentrentals.model.Room;
import studentrentals.repo.BookingRepository;
import studentrentals.repo.ListingRepository;

import java.time.LocalDate;
import java.util.List;

// Handles booking lifecycle and validation logic
public class BookingService {

    // Access to room and property data
    private final ListingRepository listingRepo;

    // Access to stored bookings
    private final BookingRepository bookingRepo;

    // Inject required repositories
    public BookingService(ListingRepository listingRepo, BookingRepository bookingRepo) {
        this.listingRepo = listingRepo;
        this.bookingRepo = bookingRepo;
    }

    // Creates a booking request for a room
    public Booking requestBooking(String studentId, String roomId,
                                  LocalDate start, LocalDate end) {

        // Validate required inputs
        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("studentId required");
        if (roomId == null || roomId.isBlank())
            throw new IllegalArgumentException("roomId required");

        // Load room details
        Room r = listingRepo.getRoom(roomId);

        // Only listed rooms can be booked
        if (!r.isListed())
            throw new IllegalStateException("Room is not listed.");

        // Ensure dates fall within room availability
        if (start != null && end != null && !r.supportsDateRange(start, end)) {
            throw new IllegalArgumentException("Dates outside availability.");
        }

        // Avoid overlap with accepted bookings
        List<Booking> existing = bookingRepo.findByRoomId(roomId);
        for (Booking b : existing) {
            if (b.getStatus() == BookingStatus.ACCEPTED &&
                start != null && end != null &&
                b.overlaps(start, end)) {

                throw new IllegalStateException("Room already booked for those dates.");
            }
        }

        // Create and store booking
        return bookingRepo.create(studentId, roomId, start, end);
    }

    // Accepts a booking request (homeowner only)
    public void acceptBooking(String homeownerId, String bookingId) {

        // Load booking
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Load related room
        Room r = listingRepo.getRoom(b.getRoomId());

        // Ensure homeowner owns the room
        if (!r.getHomeownerId().equals(homeownerId))
            throw new IllegalStateException("Not your room.");

        // Re-check overlap with other accepted bookings
        List<Booking> existing = bookingRepo.findByRoomId(b.getRoomId());
        for (Booking other : existing) {
            if (other.getStatus() == BookingStatus.ACCEPTED &&
                other.overlaps(b.getStartDate(), b.getEndDate())) {

                throw new IllegalStateException("Cannot accept: overlap with accepted booking.");
            }
        }

        // Mark booking as accepted
        b.accept();
    }

    // Rejects a booking request (homeowner only)
    public void rejectBooking(String homeownerId, String bookingId) {

        // Load booking
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Load related room
        Room r = listingRepo.getRoom(b.getRoomId());

        // Ensure homeowner owns the room
        if (!r.getHomeownerId().equals(homeownerId))
            throw new IllegalStateException("Not your room.");

        // Mark booking as rejected
        b.reject();
    }

    // Allows a student to cancel their own booking
    public void cancelBookingAsStudent(String studentId, String bookingId) {

        // Load booking
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Ensure student owns the booking
        if (!b.getStudentId().equals(studentId))
            throw new IllegalStateException("Not your booking.");

        // Cancel booking
        b.cancel();
    }
}
