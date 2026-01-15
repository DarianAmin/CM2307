package studentrentals.model;

public class Homeowner extends User {
    public Homeowner(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
    }

    @Override
    public Role getRole() { return Role.HOMEOWNER; }
}
