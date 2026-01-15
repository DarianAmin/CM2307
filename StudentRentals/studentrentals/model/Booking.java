package studentrentals.model;

import java.time.LocalDate;

public class Booking {
    private final String id;
    private final String studentId;
    private final String roomId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private BookingStatus status;

    public Booking(String id, String studentId, String roomId, LocalDate startDate, LocalDate endDate) {
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
        this.status = BookingStatus.PENDING;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getRoomId() { return roomId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingStatus getStatus() { return status; }

    public void accept() { status = BookingStatus.ACCEPTED; }
    public void reject() { status = BookingStatus.REJECTED; }
    public void cancel() { status = BookingStatus.CANCELLED; }

    public boolean overlaps(LocalDate start, LocalDate end) {
        return !startDate.isAfter(end) && !start.isAfter(endDate);
    }
}
