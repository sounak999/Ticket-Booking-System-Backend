package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ticket.booking.util.UserServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String userId;
    private String name;
    private String hashedPassword;
    private List<Ticket> ticketsBooked;

    public User() {}

    public User(String name, String password) {
        this(
            UUID.randomUUID().toString(),
            name,
            UserServiceUtil.hashPassword(password),
            new ArrayList<>()
        );
    }

    public User(String userId, String name, String hashedPassword, List<Ticket> ticketsBooked) {
        this.userId = userId;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }

    public void printTickets() {
        for (Ticket ticket: ticketsBooked) {
            System.out.println(ticket.getTicketInfo());
        }
    }
}
