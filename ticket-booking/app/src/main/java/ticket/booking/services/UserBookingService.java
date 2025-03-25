package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.util.UserServiceUtil;
import ticket.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class UserBookingService {
    private final TrainService trainService;
    private User user;
    private List<User> userList;
    private List<Train> trainList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService() throws IOException {
        trainService = new TrainService();
        loadUsers();
    }

    public UserBookingService(User user) throws IOException {
        this.user = user;
        this.trainService = new TrainService();
        loadUsers();
    }

    public Boolean loginUser(String name, String password) {
        Optional<User> foundUser = userList
                .stream().
                filter(user ->
                        user.getName().equalsIgnoreCase(name) &&
                        UserServiceUtil.checkPassword(password, user.getHashedPassword()))
                .findFirst();

        if (foundUser.isEmpty()) {
            return Boolean.FALSE;
        }

        user = foundUser.get();
        return Boolean.TRUE;
    }

    public void signUp(User user1) {
        try {
            userList.add(user1);
            saveToUserFile();
        } catch (IOException ignored) {
        }
    }

    public void fetchBooking() {
        if (user == null) {
            System.out.println("User has no bookings yet 🤔");
            return;
        }
        user.printTickets();
    }

    public List<Train> getTrains(String src, String dest) {
        this.trainList = trainService.searchTrains(src, dest);
        return trainList;
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    public Boolean bookSeat(Train train, int row, int col) throws IOException {
        if (train == null) {
            System.out.println("Train cannot be null");
            return Boolean.FALSE;
        }

        int totalRows = train.getSeats().size();
        int totalCols = train.getSeats().get(0).size();
        if (row < 1 || row > totalRows || col < 1 || col > totalCols) {
            System.out.println("Invalid row or column number");
            return Boolean.FALSE;
        }

        List<List<Integer>> seats = train.getSeats();
        if (seats.get(row - 1).get(col - 1) == 1) {
            System.out.println("Seat is already booked");
            return Boolean.FALSE;
        }

        seats.get(row - 1).set(col - 1, 1);
        train.setSeats(seats);
        trainService.updateTrain(train);
        return Boolean.TRUE;
    }

    public Boolean cancelBooking(String ticketId) throws IOException {
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket Id cannot be null or empty");
            return Boolean.FALSE;
        }

        boolean isRemoved = user.getTicketsBooked()
                .removeIf(ticket -> ticketId.equals(ticket.getTicketId()));

        if (!isRemoved) {
            System.out.println("No ticket found with ticket id: " + ticketId);
            return Boolean.FALSE;
        }

        saveToUserFile();
        System.out.println("Ticket with ticket id: " + ticketId + " has been cancelled successfully");
        return Boolean.TRUE;
    }

    private void loadUsers() throws IOException {
        File file = new File(USERS_PATH);
        if (!file.exists()) {
            System.out.println("Not a single user was found in the system 😟");
            userList = new ArrayList<>();
            return;
        }

        try {
            userList = objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error occurred while fetching trains 😟 ==> " + e.getMessage());
            throw e;
        }
    }

    private void saveToUserFile() throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);
    }
}
