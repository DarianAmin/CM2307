package studentrentals.model;

import java.time.LocalDateTime;

// Represents a review left by a student after a booking
public class Review {

    // Unique review identifier
    private final String id;

    // ID of the related booking
    private final String bookingId;

    // ID of the reviewed room
    private final String roomId;

    // ID of the student who wrote the review
    private final String studentId;

    // Star rating (1–5)
    private final int stars;

    private final String comment;

    // Timestamp when the review was created
    private final LocalDateTime createdAt;

    // Creates a new review with validation
    public Review(String id, String bookingId, String roomId,
                  String studentId, int stars, String comment) {

        // Validate required fields
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (bookingId == null || bookingId.isBlank()) throw new IllegalArgumentException("bookingId required");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId required");
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("studentId required");
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("stars must be 1-5");

        this.id = id;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.studentId = studentId;
        this.stars = stars;

        // Default to empty comment if none provided
        this.comment = comment == null ? "" : comment;

        // Capture creation time
        this.createdAt = LocalDateTime.now();
    }

    // Basic getters
    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public String getStudentId() { return studentId; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
