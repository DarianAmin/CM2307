package studentrentals;

import studentrentals.model.User;

public class Session {
    private User current;

    public void login(User user) {
        if (user == null) throw new IllegalArgumentException("user required");
        if (!user.isActive()) throw new IllegalStateException("Account is deactivated.");
        this.current = user;
    }

    public void logout() { this.current = null; }

    public User requireLoggedIn() {
        if (current == null) throw new IllegalStateException("You must login first.");
        if (!current.isActive()) throw new IllegalStateException("Account is deactivated.");
        return current;
    }
}
