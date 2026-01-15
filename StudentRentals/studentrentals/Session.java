package studentrentals;

import studentrentals.model.User;

// Tracks the currently logged-in user
public class Session {

    // The active user session
    private User current;

    // Logs a user into the session
    public void login(User user) {
        // Ensure a valid user is provided
        if (user == null)
            throw new IllegalArgumentException("user required");

        // Prevent login for inactive accounts
        if (!user.isActive())
            throw new IllegalStateException("Account is deactivated.");

        this.current = user;
    }

    // Logs the current user out
    public void logout() {
        this.current = null;
    }

    // Ensures a user is logged in before continuing
    public User requireLoggedIn() {

        // Block access if no user is logged in
        if (current == null)
            throw new IllegalStateException("You must login first.");

        // Re-check account status
        if (!current.isActive())
            throw new IllegalStateException("Account is deactivated.");

        return current;
    }
}
