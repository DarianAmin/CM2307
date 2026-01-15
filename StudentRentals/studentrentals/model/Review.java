package studentrentals.model;

import java.time.LocalDateTime;

public class Review {
    private final String id;
    private final String bookingId;
    private final String roomId;
    private final String studentId;
    private final int stars;
    private final String comment;
    private final LocalDateTime createdAt;

    public Review(String id, String bookingId, String roomId, String studentId, int stars, String comment) {
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
        this.comment = comment == null ? "" : comment;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public String getStudentId() { return studentId; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
