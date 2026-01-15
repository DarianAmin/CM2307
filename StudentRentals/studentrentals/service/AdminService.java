package studentrentals.service;

import studentrentals.model.User;
import studentrentals.repo.ListingRepository;
import studentrentals.repo.UserRepository;

import java.util.List;

public class AdminService {
    private final UserRepository userRepo;
    private final ListingRepository listingRepo;

    public AdminService(UserRepository userRepo, ListingRepository listingRepo) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
    }

    public List<User> listUsers() {
        return userRepo.findAll();
    }

    public void deactivateUserByEmail(String email) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.deactivate();
    }

    public void removeProperty(String propertyId) {
        listingRepo.adminRemoveProperty(propertyId);
    }
}
