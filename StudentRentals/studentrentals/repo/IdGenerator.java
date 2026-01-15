package studentrentals.repo;

import java.util.UUID;

public class IdGenerator {
    public String nextId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
