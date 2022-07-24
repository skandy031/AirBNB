import java.sql.*;
import java.util.Scanner;


public class Driver {
    static Connection con = null;


//    public static void handleHostLogin() {
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Enter username:");
//        String username = scan.next();
//        System.out.println("Enter password:");
//        String password = scan.next();
//        //check to see if this matches in database
//        //if it does not match
//        //print out statement
//        //call function again
//        //if it does match
//        //send to handleHost(user)
//        scan.close();
//    }


//    public void handleHost(String username) {
//        //print out all the options
//        System.out.println("(1) Create listing");
//        System.out.println("(2) View listings");
//        System.out.println("(3) Delete listing");
//
//
//    }

    public static void createAccount() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Username (Enter SIN Number):");
        int username = scan.nextInt();
        int x = 0;
        String password = "";
//        while (x == 0) {
//            System.out.println("Password:");
//            password = scan.next();
//            System.out.println("Please Input your password again");
//            if(password.equals(scan.next()))
//                x = 1;
//        }
        System.out.println("Password:");
        password = scan.next();
        System.out.println("First name:");
        String firstname = scan.next();
        System.out.println("Last name:");
        String lastname = scan.next();
        System.out.println("Occupation:");
        String occupation = scan.next();
        System.out.println("Date of birth (YYYY-MM-DD):");
        String dob = scan.next();
        scan.close();
        User.processInfo(con,username, password, firstname, lastname, occupation, dob);
        //add this user to the database
        //write prompts depending on success/fail
    }

    public static void mainMenu(Connection con) {

        //take in user input
        int option;
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Login as Host");
            System.out.println("(2) Login as Renter");
            System.out.println("(3) Create an account");
            Scanner scan = new Scanner(System.in);
            try {
                option = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid option. Must be an integer.\n");
                scan.nextLine();
            }
        }

        if (option == 0) {
            return;
        } else if (option == 1) {
            Host.handleHostLogin(con);
        } else if (option == 2) {
            Renter.handleRenterLogin();
        } else if (option == 3) {
            createAccount();
            mainMenu(con);
        } else {
            System.out.println("Invalid option.\n");
            mainMenu(con);
        }

    }

    public static void main(String[] args) {
        try {
//      Class.forName("com.mysql.jdbc.Driver");
      con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AirBNB",
        "root", "Skandium86");

      if(!con.isClosed())
        System.out.println("Successfully connected to MySQL server using TCP/IP...");
//        String sql = "TRUNCATE TABLE Hosts";
//        Statement stmt = con.createStatement();
//        stmt.executeUpdate(sql);
//
    } catch(Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }

        mainMenu(con);
    }
}
