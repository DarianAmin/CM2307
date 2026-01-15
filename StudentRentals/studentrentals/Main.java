package studentrentals;

import studentrentals.model.*;
import studentrentals.repo.*;
import studentrentals.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Demo credentials (shown to the user for reference)
    private static final String ADMIN_EMAIL = "admin@sr.com";
    private static final String ADMIN_PASSWORD = "AdminPass123!";

    private static final String HOMEOWNER_EMAIL = "hannah@sr.com";
    private static final String HOMEOWNER_PASSWORD = "HomePass123!";

    private static final String STUDENT_EMAIL = "sam@sr.com";
    private static final String STUDENT_PASSWORD = "StudPass123!";

    // UK-style date formatting
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        // ---------- Repository setup ----------
        UserRepository userRepository = new UserRepository();
        ListingRepository listingRepository = new ListingRepository();
        BookingRepository bookingRepository = new BookingRepository();
        ReviewRepository reviewRepository = new ReviewRepository();

        // ---------- Service setup ----------
        PasswordHasher passwordHasher = new PasswordHasher();
        UserFactory userFactory = new UserFactory();

        AuthService authenticationService = new AuthService(userRepository, passwordHasher, userFactory);
        SearchService searchService = new SearchService(listingRepository);
        BookingService bookingService = new BookingService(listingRepository, bookingRepository);
        ReviewService reviewService = new ReviewService(reviewRepository, listingRepository, bookingRepository);
        AdminService adminService = new AdminService(userRepository, listingRepository);

        // Seed demo data (users + 1 property + 1 room)
        seedDemoData(authenticationService, listingRepository);

        // Session + input
        Session session = new Session();
        Scanner scanner = new Scanner(System.in);

        printBanner();
        printCredentials();

        while (true) {
            System.out.println("\n====================");
            System.out.println("Main Menu");
            System.out.println("====================");
            System.out.println("1) Login");
            System.out.println("2) Show demo credentials");
            System.out.println("3) Exit");

            int chosenOption = readInt(scanner, "Choose an option: ", 1, 3);

            if (chosenOption == 1) {
                User loggedInUser;
                try {
                    loggedInUser = performLogin(scanner, authenticationService);
                } catch (Exception ex) {
                    System.out.println("Login failed: " + ex.getMessage());
                    continue;
                }

                session.login(loggedInUser);
                System.out.println("\nLogged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

                if (loggedInUser.getRole() == Role.STUDENT) {
                    studentMenu(scanner, session, searchService, bookingService, reviewService, bookingRepository, listingRepository);
                } else if (loggedInUser.getRole() == Role.HOMEOWNER) {
                    homeownerMenu(scanner, session, listingRepository, bookingRepository, bookingService);
                } else {
                    adminMenu(scanner, session, adminService);
                }

                session.logout();
                System.out.println("\nLogged out.");

            } else if (chosenOption == 2) {
                printCredentials();
            } else {
                System.out.println("\nGoodbye.");
                break;
            }
        }

        scanner.close();
    }

    // =========================================================
    // MENUS
    // =========================================================

    private static void studentMenu(
            Scanner scanner,
            Session session,
            SearchService searchService,
            BookingService bookingService,
            ReviewService reviewService,
            BookingRepository bookingRepository,
            ListingRepository listingRepository
    ) {
        while (true) {
            System.out.println("\n====================");
            System.out.println("Student Menu");
            System.out.println("====================");
            System.out.println("1) Search rooms");
            System.out.println("2) Request booking");
            System.out.println("3) View my bookings");
            System.out.println("4) Cancel my booking");
            System.out.println("5) Leave review (accepted booking only)");
            System.out.println("6) Logout");

            int chosenOption = readInt(scanner, "Choose an option: ", 1, 6);

            User currentUser = session.requireLoggedIn();
            String studentUserId = currentUser.getId();

            if (chosenOption == 1) {
                RoomSearchQuery searchQuery = readSearchQuery(scanner);
                List<Room> searchResults = searchService.search(searchQuery);

                System.out.println("\nSearch results: " + searchResults.size());
                printRooms(searchResults);

            } else if (chosenOption == 2) {
                String selectedRoomId = readNonEmpty(scanner, "Enter Room ID: ");
                LocalDate bookingStartDate = readDate(scanner, "Start date");
                LocalDate bookingEndDate = readDate(scanner, "End date");

                try {
                    Booking createdBooking = bookingService.requestBooking(studentUserId, selectedRoomId, bookingStartDate, bookingEndDate);
                    System.out.println("Booking requested: " + createdBooking.getId()
                            + " | " + createdBooking.getStartDate().format(DATE_FORMAT)
                            + " to " + createdBooking.getEndDate().format(DATE_FORMAT)
                            + " | Status: " + createdBooking.getStatus());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 3) {
                List<Booking> studentBookings = bookingRepository.findByStudentId(studentUserId);

                if (studentBookings.isEmpty()) {
                    System.out.println("You have no bookings.");
                } else {
                    System.out.println("\nMy bookings:");
                    for (Booking booking : studentBookings) {
                        System.out.println(" - " + booking.getId()
                                + " | Room " + booking.getRoomId()
                                + " | " + booking.getStartDate().format(DATE_FORMAT)
                                + " to " + booking.getEndDate().format(DATE_FORMAT)
                                + " | " + booking.getStatus());
                    }
                }

            } else if (chosenOption == 4) {
                String bookingIdToCancel = readNonEmpty(scanner, "Enter Booking ID to cancel: ");
                try {
                    bookingService.cancelBookingAsStudent(studentUserId, bookingIdToCancel);
                    System.out.println("Booking cancelled (if it was yours).");
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 5) {
                String bookingIdToReview = readNonEmpty(scanner, "Enter Booking ID to review: ");
                int ratingStars = readInt(scanner, "Stars (1-5): ", 1, 5);
                String reviewComment = readLine(scanner, "Comment (optional): ");

                try {
                    reviewService.leaveReview(studentUserId, bookingIdToReview, ratingStars, reviewComment);
                    System.out.println("Review saved.");

                    Booking booking = bookingRepository.findById(bookingIdToReview)
                            .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
                    Room reviewedRoom = listingRepository.getRoom(booking.getRoomId());
                    System.out.println("Room " + reviewedRoom.getId() + " average rating is now: "
                            + String.format("%.2f", reviewedRoom.getAverageRating()));
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else {
                return;
            }
        }
    }

    private static void homeownerMenu(
            Scanner scanner,
            Session session,
            ListingRepository listingRepository,
            BookingRepository bookingRepository,
            BookingService bookingService
    ) {
        while (true) {
            System.out.println("\n====================");
            System.out.println("Homeowner Menu");
            System.out.println("====================");
            System.out.println("1) Add property");
            System.out.println("2) Add room to property");
            System.out.println("3) View my properties");
            System.out.println("4) View pending bookings for my rooms");
            System.out.println("5) Accept booking");
            System.out.println("6) Reject booking");
            System.out.println("7) Logout");

            int chosenOption = readInt(scanner, "Choose an option: ", 1, 7);

            User currentUser = session.requireLoggedIn();
            String homeownerUserId = currentUser.getId();

            if (chosenOption == 1) {
                String propertyAddress = readNonEmpty(scanner, "Address: ");
                String cityArea = readNonEmpty(scanner, "City area (e.g., Canton): ");
                String propertyDescription = readLine(scanner, "Description (optional): ");

                try {
                    Property createdProperty = listingRepository.addProperty(homeownerUserId, propertyAddress, cityArea, propertyDescription);
                    System.out.println("Property created: " + createdProperty.getId()
                            + " | " + createdProperty.getCityArea()
                            + " | " + createdProperty.getAddress());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 2) {
                String propertyId = readNonEmpty(scanner, "Property ID: ");
                RoomType roomType = readRoomType(scanner);
                double monthlyRent = readDouble(scanner, "Monthly rent: ", 0, 1_000_000);
                String amenities = readLine(scanner, "Amenities (optional): ");
                LocalDate availableFromDate = readDate(scanner, "Available from");
                LocalDate availableToDate = readDate(scanner, "Available to");

                try {
                    Room createdRoom = listingRepository.addRoom(
                            homeownerUserId, propertyId, roomType, monthlyRent, amenities, availableFromDate, availableToDate
                    );

                    System.out.println("Room created: " + createdRoom.getId()
                            + " | Rent £" + createdRoom.getMonthlyRent()
                            + " | Available " + createdRoom.getAvailableFrom().format(DATE_FORMAT)
                            + " to " + createdRoom.getAvailableTo().format(DATE_FORMAT));
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 3) {
                List<Property> allProperties = listingRepository.findAllProperties();
                boolean foundAny = false;

                System.out.println("\nMy properties:");
                for (Property property : allProperties) {
                    if (property.getHomeownerId().equals(homeownerUserId)) {
                        foundAny = true;
                        System.out.println(" - " + property.getId()
                                + " | " + property.getCityArea()
                                + " | " + property.getAddress());
                    }
                }

                if (!foundAny) {
                    System.out.println("No properties yet.");
                }

            } else if (chosenOption == 4) {
                List<Booking> allBookings = bookingRepository.findAll();
                boolean foundPending = false;

                System.out.println("\nPending bookings for your rooms:");
                for (Booking booking : allBookings) {
                    if (booking.getStatus() == BookingStatus.PENDING) {
                        Room bookedRoom = listingRepository.getRoom(booking.getRoomId());
                        if (bookedRoom.getHomeownerId().equals(homeownerUserId)) {
                            foundPending = true;
                            System.out.println(" - " + booking.getId()
                                    + " | Room " + booking.getRoomId()
                                    + " | " + booking.getStartDate().format(DATE_FORMAT)
                                    + " to " + booking.getEndDate().format(DATE_FORMAT));
                        }
                    }
                }

                if (!foundPending) {
                    System.out.println("None pending.");
                }

            } else if (chosenOption == 5) {
                String bookingIdToAccept = readNonEmpty(scanner, "Booking ID to accept: ");
                try {
                    bookingService.acceptBooking(homeownerUserId, bookingIdToAccept);
                    System.out.println("Accepted booking: " + bookingIdToAccept);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 6) {
                String bookingIdToReject = readNonEmpty(scanner, "Booking ID to reject: ");
                try {
                    bookingService.rejectBooking(homeownerUserId, bookingIdToReject);
                    System.out.println("Rejected booking: " + bookingIdToReject);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else {
                return;
            }
        }
    }

    private static void adminMenu(Scanner scanner, Session session, AdminService adminService) {
        while (true) {
            System.out.println("\n====================");
            System.out.println("Admin Menu");
            System.out.println("====================");
            System.out.println("1) List users");
            System.out.println("2) Deactivate user by email");
            System.out.println("3) Remove property by ID");
            System.out.println("4) Logout");

            int chosenOption = readInt(scanner, "Choose an option: ", 1, 4);

            session.requireLoggedIn(); // enforce login

            if (chosenOption == 1) {
                List<User> allUsers = adminService.listUsers();
                System.out.println("\nUsers (" + allUsers.size() + "):");
                for (User user : allUsers) {
                    System.out.println(" - " + user.getEmail()
                            + " | " + user.getName()
                            + " | " + user.getRole()
                            + " | active=" + user.isActive());
                }

            } else if (chosenOption == 2) {
                String emailToDeactivate = readNonEmpty(scanner, "Email to deactivate: ");
                try {
                    adminService.deactivateUserByEmail(emailToDeactivate);
                    System.out.println("Deactivated: " + emailToDeactivate);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else if (chosenOption == 3) {
                String propertyIdToRemove = readNonEmpty(scanner, "Property ID to remove: ");
                try {
                    adminService.removeProperty(propertyIdToRemove);
                    System.out.println("Removed property: " + propertyIdToRemove);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

            } else {
                return;
            }
        }
    }

    // =========================================================
    // INPUT HELPERS
    // =========================================================

    private static int readInt(Scanner scanner, String prompt, int minValue, int maxValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int parsedValue = Integer.parseInt(input);
                if (parsedValue < minValue || parsedValue > maxValue) throw new NumberFormatException();
                return parsedValue;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number between " + minValue + " and " + maxValue + ".");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt, double minValue, double maxValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double parsedValue = Double.parseDouble(input);
                if (parsedValue < minValue || parsedValue > maxValue) throw new NumberFormatException();
                return parsedValue;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number between " + minValue + " and " + maxValue + ".");
            }
        }
    }

    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) return input;
            System.out.println("This cannot be empty.");
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static LocalDate readDate(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " (DD/MM/YYYY): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Please use DD/MM/YYYY.");
            }
        }
    }

    private static RoomType readRoomType(Scanner scanner) {
        while (true) {
            System.out.print("Room type (1=SINGLE, 2=DOUBLE): ");
            String input = scanner.nextLine().trim();
            if ("1".equals(input)) return RoomType.SINGLE;
            if ("2".equals(input)) return RoomType.DOUBLE;
            System.out.println("Please choose 1 or 2.");
        }
    }

    private static RoomSearchQuery readSearchQuery(Scanner scanner) {
        System.out.println("\nEnter search filters (press Enter to skip a filter).");

        String cityArea = readOptionalString(scanner, "City area: ");
        Double minimumPrice = readOptionalDouble(scanner, "Min price: ");
        Double maximumPrice = readOptionalDouble(scanner, "Max price: ");
        LocalDate desiredStartDate = readOptionalDate(scanner, "Start date (DD/MM/YYYY): ");
        LocalDate desiredEndDate = readOptionalDate(scanner, "End date (DD/MM/YYYY): ");
        RoomType desiredRoomType = readOptionalRoomType(scanner);

        return new RoomSearchQuery(cityArea, minimumPrice, maximumPrice, desiredStartDate, desiredEndDate, desiredRoomType);
    }

    private static String readOptionalString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isBlank() ? null : input;
    }

    private static Double readOptionalDouble(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number. Skipping this filter.");
            return null;
        }
    }

    private static LocalDate readOptionalDate(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return null;
        try {
            return LocalDate.parse(input, DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid date. Skipping this filter.");
            return null;
        }
    }

    private static RoomType readOptionalRoomType(Scanner scanner) {
        System.out.print("Room type (Enter to skip, 1=SINGLE, 2=DOUBLE): ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return null;
        if ("1".equals(input)) return RoomType.SINGLE;
        if ("2".equals(input)) return RoomType.DOUBLE;
        System.out.println("Invalid choice. Skipping room type filter.");
        return null;
    }

    private static void printRooms(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }

        for (Room room : rooms) {
            String cityArea = (room.getProperty() == null) ? "Unknown" : room.getProperty().getCityArea();
            System.out.println(" - " + room.getId()
                    + " | " + cityArea
                    + " | " + room.getType()
                    + " | £" + room.getMonthlyRent()
                    + " | Available " + room.getAvailableFrom().format(DATE_FORMAT)
                    + " to " + room.getAvailableTo().format(DATE_FORMAT)
                    + " | Rating " + String.format("%.2f", room.getAverageRating()));
        }
    }

    // =========================================================
    // AUTH + DEMO DATA
    // =========================================================

    private static User performLogin(Scanner scanner, AuthService authenticationService) {
        String email = readNonEmpty(scanner, "Email: ");
        String password = readNonEmpty(scanner, "Password: ");
        return authenticationService.login(email, password);
    }

    private static void seedDemoData(AuthService authenticationService, ListingRepository listingRepository) {
        // Admin
        authenticationService.seedAdmin(ADMIN_EMAIL, "Admin", ADMIN_PASSWORD);

        // Homeowner + Student
        authenticationService.registerHomeowner("Hannah Homeowner", HOMEOWNER_EMAIL, HOMEOWNER_PASSWORD);
        authenticationService.registerStudent("Sam Student", STUDENT_EMAIL, STUDENT_PASSWORD, "Cardiff University", "24022638");

        // Create 1 demo property + room (as homeowner)
        User homeownerAccount = authenticationService.login(HOMEOWNER_EMAIL, HOMEOWNER_PASSWORD);

        Property demoProperty = listingRepository.addProperty(
                homeownerAccount.getId(),
                "12 Example Street",
                "Canton",
                "Two-storey house near transport links"
        );

        LocalDate availableFrom = LocalDate.now().plusDays(1);
        LocalDate availableTo = LocalDate.now().plusMonths(6);

        listingRepository.addRoom(
                homeownerAccount.getId(),
                demoProperty.getId(),
                RoomType.SINGLE,
                650.00,
                "WiFi, Desk, Bills included",
                availableFrom,
                availableTo
        );
    }

    private static void printBanner() {
        System.out.println("==================================================");
        System.out.println("StudentRentals Interactive Demo");
        System.out.println("Dates: DD/MM/YYYY");
        System.out.println("==================================================");
    }

    private static void printCredentials() {
        System.out.println("\nDemo credentials (for reference):");
        System.out.println(" - Admin:     " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
        System.out.println(" - Homeowner: " + HOMEOWNER_EMAIL + " / " + HOMEOWNER_PASSWORD);
        System.out.println(" - Student:   " + STUDENT_EMAIL + " / " + STUDENT_PASSWORD);
    }
}
