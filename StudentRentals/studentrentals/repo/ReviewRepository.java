package studentrentals.repo;

import studentrentals.model.Review;
import java.util.*;

public class ReviewRepository {
    private final List<Review> reviews = new ArrayList<>();
    private final IdGenerator ids = new IdGenerator();

    public Review create(String bookingId, String roomId, String studentId, int stars, String comment) {
        Review r = new Review(ids.nextId("REV"), bookingId, roomId, studentId, stars, comment);
        reviews.add(r);
        return r;
    }

    public Optional<Review> findByBookingId(String bookingId) {
        return reviews.stream().filter(r -> r.getBookingId().equals(bookingId)).findFirst();
    }

    public List<Review> all() {
        return new ArrayList<>(reviews);
    }
}
