package studentrentals.service;

import studentrentals.model.*;

public class UserFactory {
    public User create(UserType type, UserRegistrationData d) {
        return switch (type) {
            case STUDENT -> new Student(d.id(), d.name(), d.email(), d.passwordHash(), d.university(), d.studentId());
            case HOMEOWNER -> new Homeowner(d.id(), d.name(), d.email(), d.passwordHash());
            case ADMIN -> new Administrator(d.id(), d.name(), d.email(), d.passwordHash());
        };
    }
}
