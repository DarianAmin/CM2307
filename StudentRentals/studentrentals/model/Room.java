package studentrentals.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String id;
    private final String propertyId;
    private final String homeownerId;
    private final RoomType type;
    private final double monthlyRent;
    private final String amenities;
    private final LocalDate availableFrom;
    private final LocalDate availableTo;

    private boolean listed = true;
    private Property property;
    private final List<Review> reviews = new ArrayList<>();

    public Room(String id, String propertyId, String homeownerId, RoomType type, double monthlyRent,
                String amenities, LocalDate availableFrom, LocalDate availableTo) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (propertyId == null || propertyId.isBlank()) throw new IllegalArgumentException("propertyId required");
        if (homeownerId == null || homeownerId.isBlank()) throw new IllegalArgumentException("homeownerId required");
        if (type == null) throw new IllegalArgumentException("type required");
        if (monthlyRent < 0) throw new IllegalArgumentException("monthlyRent must be >= 0");
        if (availableFrom == null || availableTo == null) throw new IllegalArgumentException("availability required");
        if (availableTo.isBefore(availableFrom)) throw new IllegalArgumentException("availableTo must be after availableFrom");

        this.id = id;
        this.propertyId = propertyId;
        this.homeownerId = homeownerId;
        this.type = type;
        this.monthlyRent = monthlyRent;
        this.amenities = amenities == null ? "" : amenities;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public String getId() { return id; }
    public String getPropertyId() { return propertyId; }
    public String getHomeownerId() { return homeownerId; }
    public RoomType getType() { return type; }
    public double getMonthlyRent() { return monthlyRent; }
    public String getAmenities() { return amenities; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public LocalDate getAvailableTo() { return availableTo; }

    public boolean isListed() { return listed; }
    public void unlist() { this.listed = false; }

    public void setProperty(Property p) { this.property = p; }
    public Property getProperty() { return property; }

    public void addReview(Review r) { reviews.add(r); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        int sum = 0;
        for (Review r : reviews) sum += r.getStars();
        return sum / (double) reviews.size();
    }

    public boolean supportsDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) return true;
        if (end.isBefore(start)) return false;
        return !start.isBefore(availableFrom) && !end.isAfter(availableTo);
    }
}
