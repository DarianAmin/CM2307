package studentrentals.repo;

import studentrentals.model.*;
import java.time.LocalDate;
import java.util.*;

// Manages properties and rooms in memory
public class ListingRepository {

    // Generates unique IDs for properties and rooms
    private final IdGenerator ids = new IdGenerator();

    // Stores properties by ID
    private final Map<String, Property> propertiesById = new HashMap<>();

    // Stores rooms by ID
    private final Map<String, Room> roomsById = new HashMap<>();

    // Creates and stores a new property
    public Property addProperty(String homeownerId, String address,
                                String cityArea, String description) {

        String id = ids.nextId("PROP");
        Property p = new Property(id, homeownerId, address, cityArea, description);
        propertiesById.put(id, p);
        return p;
    }

    // Creates and stores a new room for a property
    public Room addRoom(String homeownerId, String propertyId, RoomType type,
                        double rent, String amenities,
                        LocalDate from, LocalDate to) {

        // Ensure property exists
        Property p = getProperty(propertyId);

        // Ensure homeowner owns the property
        if (!p.getHomeownerId().equals(homeownerId))
            throw new IllegalStateException("Not your property.");

        String id = ids.nextId("ROOM");

        // Create room linked to property
        Room r = new Room(id, propertyId, homeownerId, type, rent, amenities, from, to);
        r.setProperty(p);

        roomsById.put(id, r);
        return r;
    }

    // Retrieves a property by ID
    public Property getProperty(String id) {
        Property p = propertiesById.get(id);
        if (p == null)
            throw new IllegalArgumentException("Property not found: " + id);
        return p;
    }

    // Retrieves a room by ID
    public Room getRoom(String id) {
        Room r = roomsById.get(id);
        if (r == null)
            throw new IllegalArgumentException("Room not found: " + id);

        // Attach property reference if available
        Property p = propertiesById.get(r.getPropertyId());
        if (p != null) r.setProperty(p);

        return r;
    }

    // Returns all currently listed rooms
    public List<Room> findAllRoomsListed() {
        List<Room> out = new ArrayList<>();

        for (Room r : roomsById.values()) {

            // Only include listed rooms
            if (r.isListed()) {

                // Attach property details
                Property p = propertiesById.get(r.getPropertyId());
                if (p != null) r.setProperty(p);

                out.add(r);
            }
        }
        return out;
    }

    // Returns all properties
    public List<Property> findAllProperties() {
        return new ArrayList<>(propertiesById.values());
    }

    // Removes a property and all its rooms (admin only)
    public void adminRemoveProperty(String propertyId) {

        // Remove all rooms belonging to the property
        roomsById.values().removeIf(r -> r.getPropertyId().equals(propertyId));

        // Remove the property itself
        propertiesById.remove(propertyId);
    }

    // Adds a review to a room
    public void addReviewToRoom(String roomId, Review review) {
        getRoom(roomId).addReview(review);
    }
}
