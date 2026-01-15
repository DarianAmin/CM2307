package studentrentals.model;

import java.time.LocalDate;

// Represents a booking made by a student for a room
public class Booking {

    // Unique booking identifier
    private final String id;

    // ID of the student who made the booking
    private final String studentId;

    // ID of the booked room
    private final String roomId;

    // Booking start date
    private final LocalDate startDate;

    // Booking end date
    private final LocalDate endDate;

    // Current booking status
    private BookingStatus status;

    // Creates a new booking with validation
    public Booking(String id, String studentId, String roomId,
                   LocalDate startDate, LocalDate endDate) {

        // Validate required fields
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("studentId required");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId required");
        if (startDate == null || endDate == null) throw new IllegalArgumentException("dates required");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("end must be after start");

        this.id = id;
        this.studentId = studentId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;

        // New bookings start as pending
        this.status = BookingStatus.PENDING;
    }

    // Basic getters
    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getRoomId() { return roomId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingStatus getStatus() { return status; }

    // Marks the booking as accepted
    public void accept() { status = BookingStatus.ACCEPTED; }

    // Marks the booking as rejected
    public void reject() { status = BookingStatus.REJECTED; }

    // Cancels the booking
    public void cancel() { status = BookingStatus.CANCELLED; }

    // Checks if this booking overlaps a given date range
    public boolean overlaps(LocalDate start, LocalDate end) {
        return !startDate.isAfter(end) && !start.isAfter(endDate);
    }
}
