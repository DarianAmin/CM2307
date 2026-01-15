package studentrentals.model;

public class Administrator extends User {
    public Administrator(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
    }

    @Override
    public Role getRole() { return Role.ADMIN; }
}
