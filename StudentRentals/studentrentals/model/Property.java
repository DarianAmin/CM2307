package studentrentals.model;

public class Property {
    private final String id;
    private final String homeownerId;
    private String address;
    private String cityArea;
    private String description;

    public Property(String id, String homeownerId, String address, String cityArea, String description) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (homeownerId == null || homeownerId.isBlank()) throw new IllegalArgumentException("homeownerId required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("address required");
        if (cityArea == null || cityArea.isBlank()) throw new IllegalArgumentException("cityArea required");
        this.id = id;
        this.homeownerId = homeownerId;
        this.address = address;
        this.cityArea = cityArea;
        this.description = description == null ? "" : description;
    }

    public String getId() { return id; }
    public String getHomeownerId() { return homeownerId; }
    public String getAddress() { return address; }
    public String getCityArea() { return cityArea; }
    public String getDescription() { return description; }
}
