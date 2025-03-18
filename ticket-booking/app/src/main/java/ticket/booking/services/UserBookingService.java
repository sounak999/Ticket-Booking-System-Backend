package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.util.UserServiceUtil;
import ticket.booking.entities.User;
import ticket.booking.entities.Ticket;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "ticket-booking/app/src/main/java/ticket/booking/localDb/users.json";

    UserBookingService(User user) throws IOException {
        this.user = user;
        File file = new File(USERS_PATH);
        userList = objectMapper.readValue(file, new TypeReference<List<User>>() {});
    }

    public Boolean loginUser() {
        Optional<User> foundUser = userList
                .stream().
                filter(user1 ->
                        user1.getName().equalsIgnoreCase(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword()))
                .findFirst();

        return foundUser.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveToUserFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    public void fetchBooking() {
        user.printTickets();
    }

    public void cancelBooking(String ticketId) {
        // Todo: Boolean or void
    }

    private void saveToUserFile() throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);
    }
}
