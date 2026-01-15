package studentrentals.service;

import studentrentals.model.Room;
import studentrentals.repo.ListingRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private final ListingRepository listingRepo;

    public SearchService(ListingRepository listingRepo) {
        this.listingRepo = listingRepo;
    }

    public List<Room> search(RoomSearchQuery q) {
        List<Room> all = listingRepo.findAllRoomsListed();
        if (q == null) return all;
        List<Room> out = new ArrayList<>();
        for (Room r : all) {
            if (q.matches(r)) out.add(r);
        }
        return out;
    }
}
