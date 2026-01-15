package studentrentals.service;

import studentrentals.model.User;
import studentrentals.repo.IdGenerator;
import studentrentals.repo.UserRepository;

// Handles registration and login logic
public class AuthService {

    // Access to stored users
    private final UserRepository userRepo;

    // Password hashing utility
    private final PasswordHasher hasher;

    // Creates users based on role
    private final UserFactory factory;

    // Generates unique user IDs
    private final IdGenerator ids = new IdGenerator();

    // Inject required dependencies
    public AuthService(UserRepository userRepo, PasswordHasher hasher, UserFactory factory) {
        this.userRepo = userRepo;
        this.hasher = hasher;
        this.factory = factory;
    }

    // Registers a new student user
    public void registerStudent(String name, String email, String rawPassword,
                                String university, String studentId) {

        // Validate common fields
        validateBasics(name, email, rawPassword);

        // Validate student-specific data
        if (university == null || university.isBlank())
            throw new IllegalArgumentException("University required.");
        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("Student ID required.");

        // Ensure email is unique
        ensureEmailFree(email);

        // Hash password before storing
        String hash = hasher.hash(rawPassword);

        // Create registration payload
        var data = new UserRegistrationData(
                ids.nextId("USR"), name, email, hash, university, studentId
        );

        // Create and save student user
        userRepo.save(factory.create(UserType.STUDENT, data));
    }

    // Registers a new homeowner user
    public void registerHomeowner(String name, String email, String rawPassword) {

        // Validate common fields
        validateBasics(name, email, rawPassword);

        // Ensure email is unique
        ensureEmailFree(email);

        // Hash password before storing
        String hash = hasher.hash(rawPassword);

        // Create registration payload
        var data = new UserRegistrationData(
                ids.nextId("USR"), name, email, hash, null, null
        );

        // Create and save homeowner user
        userRepo.save(factory.create(UserType.HOMEOWNER, data));
    }

    // Authenticates a user and returns the account
    public User login(String email, String rawPassword) {

        // Validate login input
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email required.");
        if (rawPassword == null || rawPassword.isBlank())
            throw new IllegalArgumentException("Password required.");

        // Look up user by email
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        // Block login for inactive accounts
        if (!u.isActive())
            throw new IllegalStateException("Account is deactivated.");

        // Verify password hash
        String hash = hasher.hash(rawPassword);
        if (!u.getPasswordHash().equals(hash))
            throw new IllegalArgumentException("Invalid credentials.");

        return u;
    }

    // Creates an admin account if one does not exist
    public void seedAdmin(String email, String name, String password) {

        // Do nothing if admin already exists
        if (userRepo.findByEmail(email).isPresent()) return;

        // Hash admin password
        String hash = hasher.hash(password);

        // Create admin registration payload
        var data = new UserRegistrationData(
                ids.nextId("USR"), name, email, hash, null, null
        );

        // Create and save admin user
        userRepo.save(factory.create(UserType.ADMIN, data));
    }

    // Validates shared registration fields
    private void validateBasics(String name, String email, String pw) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name required.");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Valid email required.");
        if (pw == null || pw.length() < 8)
            throw new IllegalArgumentException("Password must be 8+ chars.");
    }

    // Ensures the email has not already been registered
    private void ensureEmailFree(String email) {
        if (userRepo.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Email already registered.");
    }
}
