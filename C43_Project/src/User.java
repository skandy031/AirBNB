import java.sql.*;
import java.util.Scanner;

public class User {
    public static void processInfo(Connection con, int sin, String password, String firstName, String lastName, String occupation,
                                   String dob) {
        try {
            String query = "INSERT INTO USERS VALUES(%s, '%s', %s, %s, %s)".format(String.valueOf(sin), password, firstName, lastName,
                    occupation, dob);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }

    }
}
