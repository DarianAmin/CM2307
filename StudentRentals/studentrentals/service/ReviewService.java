package studentrentals.service;

import studentrentals.model.Booking;
import studentrentals.model.BookingStatus;
import studentrentals.model.Review;
import studentrentals.repo.BookingRepository;
import studentrentals.repo.ListingRepository;
import studentrentals.repo.ReviewRepository;

public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final ListingRepository listingRepo;
    private final BookingRepository bookingRepo;

    public ReviewService(ReviewRepository reviewRepo, ListingRepository listingRepo, BookingRepository bookingRepo) {
        this.reviewRepo = reviewRepo;
        this.listingRepo = listingRepo;
        this.bookingRepo = bookingRepo;
    }

    public void leaveReview(String studentId, String bookingId, int stars, String comment) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("studentId required");
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!b.getStudentId().equals(studentId)) throw new IllegalStateException("Not your booking.");
        if (b.getStatus() != BookingStatus.ACCEPTED) throw new IllegalStateException("Can only review accepted bookings.");
        if (reviewRepo.findByBookingId(bookingId).isPresent()) throw new IllegalStateException("Review already exists.");

        Review r = reviewRepo.create(bookingId, b.getRoomId(), studentId, stars, comment);
        listingRepo.addReviewToRoom(b.getRoomId(), r);
    }
}
