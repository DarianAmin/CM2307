package studentrentals.repo;

import studentrentals.model.*;
import java.time.LocalDate;
import java.util.*;

public class ListingRepository {
    private final IdGenerator ids = new IdGenerator();
    private final Map<String, Property> propertiesById = new HashMap<>();
    private final Map<String, Room> roomsById = new HashMap<>();

    public Property addProperty(String homeownerId, String address, String cityArea, String description) {
        String id = ids.nextId("PROP");
        Property p = new Property(id, homeownerId, address, cityArea, description);
        propertiesById.put(id, p);
        return p;
    }

    public Room addRoom(String homeownerId, String propertyId, RoomType type, double rent, String amenities,
                        LocalDate from, LocalDate to) {
        Property p = getProperty(propertyId);
        if (!p.getHomeownerId().equals(homeownerId)) throw new IllegalStateException("Not your property.");
        String id = ids.nextId("ROOM");
        Room r = new Room(id, propertyId, homeownerId, type, rent, amenities, from, to);
        r.setProperty(p);
        roomsById.put(id, r);
        return r;
    }

    public Property getProperty(String id) {
        Property p = propertiesById.get(id);
        if (p == null) throw new IllegalArgumentException("Property not found: " + id);
        return p;
    }

    public Room getRoom(String id) {
        Room r = roomsById.get(id);
        if (r == null) throw new IllegalArgumentException("Room not found: " + id);
        Property p = propertiesById.get(r.getPropertyId());
        if (p != null) r.setProperty(p);
        return r;
    }

    public List<Room> findAllRoomsListed() {
        List<Room> out = new ArrayList<>();
        for (Room r : roomsById.values()) {
            if (r.isListed()) {
                Property p = propertiesById.get(r.getPropertyId());
                if (p != null) r.setProperty(p);
                out.add(r);
            }
        }
        return out;
    }

    public List<Property> findAllProperties() {
        return new ArrayList<>(propertiesById.values());
    }

    public void adminRemoveProperty(String propertyId) {
        roomsById.values().removeIf(r -> r.getPropertyId().equals(propertyId));
        propertiesById.remove(propertyId);
    }

    public void addReviewToRoom(String roomId, Review review) {
        getRoom(roomId).addReview(review);
    }
}
