import java.util.Scanner;
import java.sql.*;

public class Renter {

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
            PreparedStatement s = con.prepareStatement("insert into Users values (?,?,?,?,?,?)");
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
        handleRenter(username);
    }
    public static void handleRenterLogin(Connection connection){
        con = connection;
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter username:");
        int username = scan.nextInt();
        System.out.println("Enter password:");
        String password = scan.next();
//        scan.nextLine();
        scan.close();
        //check to see if this matches in database
        try {
            PreparedStatement s = con.prepareStatement("select * from users join renter where " +
                    "users.sin = renter.renterid and sin = ? and password = ?");
            s.setInt(1, username);
            s.setString(2, password);
            ResultSet rs = s.executeQuery();
            if (rs.next()){
                //entry exists
                handleRenter(username);
            } else {
                System.out.println("Username and password do not match.\n");
                handleRenterLogin(con);
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    public static void handleRenter(int username){
        //take in user input
        int option = -1;
//        while (true) {
//            System.out.println("Choose an option:");
//            System.out.println("(0) Exit System");
//            System.out.println("(1) Create reservation");
//            System.out.println("(2) View Reservations");
//            System.out.println("(3) Cancel Reservation");
//            System.out.println("(4) Delete Account");
//            Scanner scan = new Scanner(System.in);
//            try {
//                if (scan.hasNext()) {
//                    option = scan.nextInt();
//                    break;
//                }
//            } catch (Exception e) {
//                System.out.println("Invalid option. Must be an integer.\n");
//                scan.nextLine();
//            }
//        }
        System.out.println("Choose an option:");
        System.out.println("(0) Exit System");
        System.out.println("(1) Create reservation");
        System.out.println("(2) View Reservations");
        System.out.println("(3) Cancel Reservation");
        System.out.println("(4) Delete Account");
        Scanner scan = new Scanner(System.in);
        try {
            if (scan.hasNext()) {
                option = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println("Invalid option. Must be an integer.\n");
//            scan.nextLine();
            scan.close();
//            handleRenter(username);
        }

        if (option == 0) {
            return;
        } else if (option == 1) {
            // create reservation
        } else if (option == 2) {
            // view reservation
        } else if (option == 3){
            //delete reservation
        }else if (option == 4){
            //delete account
            deleteAccount(username);
        }else {
            System.out.println("Invalid option.\n");
            scan.close();
//            handleRenter(username);
        }
    }

    public static void deleteAccount(int username){
        try {
            PreparedStatement s = con.prepareStatement("delete from Users where sin = " + username);
            int status = s.executeUpdate();
            if (status == 1) {
                System.out.println("Successfully deleted.\n");
                Driver.mainMenu();
            } else{
                System.out.println("Account was not deleted.\n");
                handleRenter(username);
            }
            return;
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    public void showReservations(int username){
        // display all the reservations
    }

    public void deleteReservation(int username){
        showReservations(username);
        int option = -1;
        Scanner scan = new Scanner(System.in);
        while (true){
            try {
                System.out.println("Choose a reservation ID to cancel (-1 to exit):");
                option = scan.nextInt();
                break;
            } catch (Exception e){
                System.out.println("Invalid.\n");
            }
        }
        //search for the reservation and if exists delete it from both renter and make change in host
        if (option == -1){
            handleRenter(username);
            return;
        }
        scan.close();
    }

    public void createReservation(int username){
        //get dates
        //choose city
        //display all the listings that are available
        //prompt choosing an option
    }
}
