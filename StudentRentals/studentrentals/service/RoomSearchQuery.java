package studentrentals.service;

import studentrentals.model.Room;
import studentrentals.model.RoomType;

import java.time.LocalDate;

public record RoomSearchQuery(
        String cityArea,
        Double minPrice,
        Double maxPrice,
        LocalDate startDate,
        LocalDate endDate,
        RoomType roomType
) {
    public boolean matches(Room r) {
        if (cityArea != null && r.getProperty() != null) {
            if (!r.getProperty().getCityArea().equalsIgnoreCase(cityArea)) return false;
        }
        if (minPrice != null && r.getMonthlyRent() < minPrice) return false;
        if (maxPrice != null && r.getMonthlyRent() > maxPrice) return false;
        if (roomType != null && r.getType() != roomType) return false;

        if (startDate != null && endDate != null) {
            if (!r.supportsDateRange(startDate, endDate)) return false;
        }
        return true;
    }
}
