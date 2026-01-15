package studentrentals.model;

public abstract class User {
    private final String id;
    private String name;
    private final String email;
    private final String passwordHash;
    private boolean active = true;

    protected User(String id, String name, String email, String passwordHash) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("passwordHash required");
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
    }

    public abstract Role getRole();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.name = name;
    }
}
