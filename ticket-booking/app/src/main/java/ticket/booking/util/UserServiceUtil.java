package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String normalPassword, String hashedPassword) {
        return BCrypt.checkpw(normalPassword, hashedPassword);
    }

    public static void main(String[] args) {
        String hashedPassword = hashPassword("india789");
        System.out.println(hashedPassword);
        System.out.println(checkPassword("password", hashedPassword));
        System.out.println(checkPassword("india789", hashedPassword));
    }
}
