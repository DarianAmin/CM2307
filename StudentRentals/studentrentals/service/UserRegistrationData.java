package studentrentals.service;

public record UserRegistrationData(
        String id,
        String name,
        String email,
        String passwordHash,
        String university,
        String studentId
) { }
