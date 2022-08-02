import java.sql.*;
import java.util.Scanner;


public class Driver {
    static Connection con = null;
    static Scanner scan = new Scanner(System.in);


//    public static void createAccount() {
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Username (Enter SIN Number):");
//        int username = scan.nextInt();
//        System.out.println("Password:");
//        String password = scan.next();
//        System.out.println("First name:");
//        String firstname = scan.next();
//        System.out.println("Last name:");
//        String lastname = scan.next();
//        System.out.println("Occupation:");
//        String occupation = scan.next();
//        System.out.println("Date of birth (YYYY-MM-DD):");
//        String dob = scan.next();
////        User.processInfo(con, username, password, firstname, lastname, occupation, dob);
//        try {
//            PreparedStatement s = con.prepareStatement("insert into users values (?,?,?,?,?,?)");
//            s.setInt(1, username);
//            s.setString(2, password);
//            s.setString(3, firstname);
//            s.setString(4, lastname);
//            s.setString(5, occupation);
//            s.setString(6, dob);
//            int status = s.executeUpdate();
//            if (status == 1){
//                System.out.println("Successfully created.");
//            } else{
//                System.out.println("Not able to create account.");
//            }
//        } catch (Exception e){
//            System.out.println(e);
//        }
//        //add this user to the database
//        //write prompts depending on success/fail
//    }

    public static void mainMenu() {

        //take in user input
        int option = -1;
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Login as Host");
            System.out.println("(2) Login as Renter");
            System.out.println("(3) Create a renter account");
            System.out.println("(4) Create a host account");
            Scanner scan = new Scanner(System.in);
            try {
                option = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid option. Must be an integer.\n");
                scan.nextLine();
            }
        }
        try {
            if (option == 0) {
                return;
            } else if (option == 1) {
                Host.handleHostLogin(con);
            } else if (option == 2) {
                Renter.handleRenterLogin(con);
            } else if (option == 3) {
                Renter.createAccount(con);
                return;
            } else if (option == 4){
                Host.createAccount(con);
            }else {
                System.out.println("Invalid option.\n");
                mainMenu();
                return;
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
//      Class.forName("com.mysql.jdbc.Driver");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AirBNB",
//                    "root", "Skandium86");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/final_project",
                    "root", "Skandium86");

            if (!con.isClosed())
                System.out.println("Successfully connected to MySQL server using TCP/IP...");
//        String sql = "TRUNCATE TABLE Hosts";
//        Statement stmt = con.createStatement();
//        stmt.executeUpdate(sql);
//
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }

        mainMenu();
    }
}
