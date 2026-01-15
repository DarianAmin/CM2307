package studentrentals.service;

import studentrentals.model.Booking;
import studentrentals.model.BookingStatus;
import studentrentals.model.Room;
import studentrentals.repo.BookingRepository;
import studentrentals.repo.ListingRepository;

import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private final ListingRepository listingRepo;
    private final BookingRepository bookingRepo;

    public BookingService(ListingRepository listingRepo, BookingRepository bookingRepo) {
        this.listingRepo = listingRepo;
        this.bookingRepo = bookingRepo;
    }

    public Booking requestBooking(String studentId, String roomId, LocalDate start, LocalDate end) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("studentId required");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId required");
        Room r = listingRepo.getRoom(roomId);
        if (!r.isListed()) throw new IllegalStateException("Room is not listed.");
        if (start != null && end != null && !r.supportsDateRange(start, end)) {
            throw new IllegalArgumentException("Dates outside availability.");
        }

        // avoid overlap with accepted bookings
        List<Booking> existing = bookingRepo.findByRoomId(roomId);
        for (Booking b : existing) {
            if (b.getStatus() == BookingStatus.ACCEPTED && start != null && end != null && b.overlaps(start, end)) {
                throw new IllegalStateException("Room already booked for those dates.");
            }
        }

        return bookingRepo.create(studentId, roomId, start, end);
    }

    public void acceptBooking(String homeownerId, String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Room r = listingRepo.getRoom(b.getRoomId());
        if (!r.getHomeownerId().equals(homeownerId)) throw new IllegalStateException("Not your room.");

        // re-check overlap with other accepted bookings
        List<Booking> existing = bookingRepo.findByRoomId(b.getRoomId());
        for (Booking other : existing) {
            if (other.getStatus() == BookingStatus.ACCEPTED && other.overlaps(b.getStartDate(), b.getEndDate())) {
                throw new IllegalStateException("Cannot accept: overlap with accepted booking.");
            }
        }
        b.accept();
    }

    public void rejectBooking(String homeownerId, String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Room r = listingRepo.getRoom(b.getRoomId());
        if (!r.getHomeownerId().equals(homeownerId)) throw new IllegalStateException("Not your room.");
        b.reject();
    }

    public void cancelBookingAsStudent(String studentId, String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!b.getStudentId().equals(studentId)) throw new IllegalStateException("Not your booking.");
        b.cancel();
    }
}
