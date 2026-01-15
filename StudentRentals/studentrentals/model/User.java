package studentrentals.model;

// Base class for all user types
public abstract class User {

    // Unique user identifier
    private final String id;

    // User's display name
    private String name;

    // Login email (stored lowercase)
    private final String email;

    // Hashed password for security
    private final String passwordHash;

    // Indicates whether the account is active
    private boolean active = true;

    // Creates a user with required details
    protected User(String id, String name, String email, String passwordHash) {

        // Validate required fields
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (passwordHash == null || passwordHash.isBlank())
            throw new IllegalArgumentException("passwordHash required");

        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
    }

    // Returns the role of the user
    public abstract Role getRole();

    // Basic getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    // Checks if the account is active
    public boolean isActive() { return active; }

    // Deactivates the user account
    public void deactivate() { this.active = false; }

    // Updates the user's name
    public void setName(String name) {

        // Validate new name
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name required");

        this.name = name;
    }
}
