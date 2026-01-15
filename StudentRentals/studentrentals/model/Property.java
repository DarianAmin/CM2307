package studentrentals.model;

// Represents a property owned by a homeowner
public class Property {

    // Unique property identifier
    private final String id;

    // ID of the homeowner who owns the property
    private final String homeownerId;

    // Property address
    private String address;

    // Area of the city where the property is located
    private String cityArea;

    // property description
    private String description;

    // Creates a new property with validation
    public Property(String id, String homeownerId,
                    String address, String cityArea, String description) {

        // Validate required fields
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (homeownerId == null || homeownerId.isBlank()) throw new IllegalArgumentException("homeownerId required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("address required");
        if (cityArea == null || cityArea.isBlank()) throw new IllegalArgumentException("cityArea required");

        this.id = id;
        this.homeownerId = homeownerId;
        this.address = address;
        this.cityArea = cityArea;

        // Default to empty description if none provided
        this.description = description == null ? "" : description;
    }

    // Basic getters
    public String getId() { return id; }
    public String getHomeownerId() { return homeownerId; }
    public String getAddress() { return address; }
    public String getCityArea() { return cityArea; }
    public String getDescription() { return description; }
}
