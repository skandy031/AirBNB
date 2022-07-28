import java.util.HashSet;
import java.util.Scanner;
import java.sql.*;

public class Renter {

    static Connection con = null;
    static Scanner scan = new Scanner(System.in);

    public static void createAccount(Connection connection) {
        con = connection;
        int username = 0, ccNumber = 0, ccMonth = 0, ccYear = 0, cvc = -1;
        String password = "", firstname="", lastname="", occupation = "", dob = "";
        try {
            System.out.println("Username (Enter SIN Number):");
            username = scan.nextInt();
            System.out.println("Password:");
            password = scan.next();
            System.out.println("First name:");
            firstname = scan.next();
            System.out.println("Last name:");
            lastname = scan.next();
            System.out.println("Occupation:");
            occupation = scan.next();
            System.out.println("Date of birth (YYYY-MM-DD):");
            dob = scan.next();
            System.out.println("Credit Card Number:");
            ccNumber = scan.nextInt();
            System.out.println("Credit Card Expiry Month:");
            ccMonth = scan.nextInt();
            System.out.println("Credit Card Expiry Year:");
            ccYear = scan.nextInt();
            System.out.println("Credit Card CVC:");
            cvc = scan.nextInt();
        } catch (Exception e){
            System.out.println(e);
            createAccount(con);
        }
        try {
            PreparedStatement s = con.prepareStatement("insert into Users values (?,?,?,?,?,?)");
            s.setInt(1, username);
            s.setString(2, password);
            s.setString(3, firstname);
            s.setString(4, lastname);
            s.setString(5, occupation);
            s.setString(6, dob);
            PreparedStatement s2 = con.prepareStatement("insert into Renter values (?,?,?,?,?)");
            s2.setInt(1, username);
            s2.setInt(2, ccNumber);
            s2.setInt(3, ccMonth);
            s2.setInt(4, ccYear);
            s2.setInt(5, cvc);

            int status = s.executeUpdate();
            int status2 = s2.executeUpdate();
            if ((status == 1) && (status2 == 1)){
                System.out.println("Successfully created.");
            } else{
                System.out.println("Not able to create account.");
            }
        } catch (Exception e){
            System.out.println(e);
            System.out.println("Unsuccessful.");
            createAccount(con);
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
        int option;
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Create Reservation");
            System.out.println("(2) View Reservations");
            System.out.println("(3) Cancel Reservation");
            System.out.println("(4) Delete Account");
            System.out.println("(5) Write a Review");
            Scanner scan = new Scanner(System.in);
            try {
                if (scan.hasNext()) {
                    option = scan.nextInt();
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid option. Must be an integer.\n");
            }
        }

        if (option == 0) {
            scan.close();
        } else if (option == 1) {
            // create reservation
        } else if (option == 2) {
            // view reservation
            showReservations(username);
        } else if (option == 3){
            //delete reservation
        }else if (option == 4){
            //delete account
            deleteAccount(username);
        } else if (option == 5){
            //write a review on past stay
            writeReview(username);
        }else {
            System.out.println("Invalid option.\n");
            handleRenter(username);
        }
    }

    public static void deleteAccount(int username){
        try {
            PreparedStatement s = con.prepareStatement("delete from Users where sin = " + username);
            PreparedStatement s2 = con.prepareStatement("delete from Renter where renterID = " + username);
            int status1 = s.executeUpdate();
            int status2 = s2.executeUpdate();
            if ((status1 == 1) && (status2 == 1)) {
                System.out.println("Successfully deleted.\n");
                Driver.mainMenu();
            } else{
                System.out.println("Account was not deleted.\n");
                handleRenter(username);
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    public static HashSet<Integer> printResTable(int username){
        HashSet<Integer> listSet = new HashSet<>();
        try {
            PreparedStatement s = con.prepareStatement("select reserved.reservationID, listing.listid, listing.hostid, listing.price, " +
                    "startdate, enddate, streetno, streetname, city, province, postalcode, unitno from located join " +
                    "listing join reserved join address where address.addressid = located.addressid " +
                    "and listing.listid " + "= reserved.listid and listing.listid = located.listid");
            ResultSet rs = s.executeQuery();
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s | %5$-10s | %6$-15s | %7$-15s " +
                    "| %8$-10s | %9$-10s | %10$-15s | %11$-10s | %12$-10s";
            System.out.println(String.format(format, "ReservationID", "ListID", "HostID", "Price", "Start Date", "End Date",
                    "House Number", "Street Name", "City", "Province", "Postal Code", "Unit No."));
            String under = "_";
            for (int i = 0; i < 150; i++){
                under += "_";
            }
            System.out.println(under);

            while (rs.next()){
                int reservationID = rs.getInt("reservationID");
                listSet.add(reservationID);
                int listID = rs.getInt("listID");
                int hostID = rs.getInt("hostID");
                int price = rs.getInt("price");
                String startDate = rs.getString("startdate");
                String endDate = rs.getString("enddate");
                int streetNo = rs.getInt("streetno");
                String streetName = rs.getString("streetname");
                String city = rs.getString("city");
                String province = rs.getString("province");
                String postalCode = rs.getString("postalcode");
                int unitNo = rs.getInt("unitno");
                System.out.println(String.format(format, reservationID+"", listID+"", hostID+"", price+"", startDate, endDate,
                        streetNo+"", streetName, city, province, postalCode, unitNo+""));

            }
        } catch (Exception e){
            System.out.println(e);
        }
        return listSet;
    }

    public static void showReservations(int username){
        printResTable(username);
        handleRenter(username);
    }

    public static void deleteReservation(int username){
        printResTable(username);
        int option;
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
        }
    }

    public static void createReservation(int username){
        //get dates
        System.out.println("Starting date of reservation:");
        String startDate = scan.next();
        System.out.println("Ending date of reservation:");
        String endDate = scan.next();

        //choose city
        System.out.println("City:");
        String city = scan.next();

        //look thru each listing
        //3 options
            // not in reserved -> add to set
            // in reserved with no overlapping dates
            // in reserved with overlapping dates

        //display all the listings that are available
        //prompt choosing an option
    }

    public static void writeReview(int username) {

    }
}
