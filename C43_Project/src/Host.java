import java.sql.*;
import java.util.Scanner;

public class Host {

    static Connection con = null;

    public static void createAccount(Connection connection) {
        con = connection;
        Scanner scan = new Scanner(System.in);
        System.out.println("Username (Enter SIN Number):");
        int username = scan.nextInt();
        System.out.println("Password:");
        String password = scan.next();
        System.out.println("First name:");
        String firstname = scan.next();
        System.out.println("Last name:");
        String lastname = scan.next();
        System.out.println("Occupation:");
        String occupation = scan.next();
        System.out.println("Date of birth (YYYY-MM-DD):");
        String dob = scan.next();
        scan.close();
//        User.processInfo(con, username, password, firstname, lastname, occupation, dob);
        try {
            PreparedStatement s = con.prepareStatement("insert into users values (?,?,?,?,?,?)");
            s.setInt(1, username);
            s.setString(2, password);
            s.setString(3, firstname);
            s.setString(4, lastname);
            s.setString(5, occupation);
            s.setString(6, dob);
            int status = s.executeUpdate();
            if (status == 1){
                System.out.println("Successfully created.");
            } else{
                System.out.println("Not able to create account.");
            }
        } catch (Exception e){
            System.out.println(e);
        }
        //add this user to the database
        //write prompts depending on success/fail
    }
      public static void handleHostLogin(Connection con) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scan.next();
        System.out.println("Enter password:");
        String password = scan.next();
        //check to see if this matches in database
        //if it does not match
        //print out statement
        //call function again
        //if it does match
        //send to handleHost(user)
        scan.close();
    }


    public void handleHost(String username) {
        //print out all the options
        System.out.println("(1) Create listing");
        System.out.println("(2) View listings");
        System.out.println("(3) Delete listing");


    }
}
