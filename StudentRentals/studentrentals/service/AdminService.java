package studentrentals.service;

import studentrentals.model.User;
import studentrentals.repo.ListingRepository;
import studentrentals.repo.UserRepository;

import java.util.List;

// Provides admin-only management operations
public class AdminService {

    // Access to user storage
    private final UserRepository userRepo;

    // Access to property and room storage
    private final ListingRepository listingRepo;

    // Inject required repositories
    public AdminService(UserRepository userRepo, ListingRepository listingRepo) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
    }

    // Returns all registered users
    public List<User> listUsers() {
        return userRepo.findAll();
    }

    // Deactivates a user account by email
    public void deactivateUserByEmail(String email) {

        // Look up user or fail
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Disable the account
        u.deactivate();
    }

    // Removes a property and its rooms from the system
    public void removeProperty(String propertyId) {
        listingRepo.adminRemoveProperty(propertyId);
    }
}
