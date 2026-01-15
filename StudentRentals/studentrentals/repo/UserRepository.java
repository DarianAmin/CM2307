package studentrentals.repo;

import studentrentals.model.User;
import java.util.*;

public class UserRepository {
    private final Map<String, User> byId = new HashMap<>();
    private final Map<String, User> byEmail = new HashMap<>();

    public void save(User u) {
        byId.put(u.getId(), u);
        byEmail.put(u.getEmail().toLowerCase(), u);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(byEmail.get(email.toLowerCase()));
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }
}
