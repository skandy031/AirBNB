import java.util.HashSet;
import java.util.Scanner;
import java.sql.*;

public class Renter {

    static Connection con = null;
    static Scanner scan = new Scanner(System.in);
    static int username;

    public static void handleRenter(int input_username){
        username = input_username;
        //take in user input
        int option;
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Create Reservation");
            System.out.println("(2) View Reservations");
            System.out.println("(3) Cancel Reservation");
            System.out.println("(4) Write a Review");
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
            Driver.mainMenu();
        } else if (option == 1) {
            // create reservation
            createReservation(username);
        } else if (option == 2) {
            // view reservation
            showReservations(username);
        } else if (option == 3){
            //delete reservation
            cancelReservation(username);
        } else if (option == 4){
            //write a review on past stay
            writeReview(username);
        }else {
            System.out.println("Invalid option.\n");
            handleRenter(username);
        }
    }



    public static HashSet<Integer> printResTable(int username){
        HashSet<Integer> reserveSet = new HashSet<>();
        try {
            PreparedStatement s = con.prepareStatement("select reserved.reservationID, " +
                    "listing.listid, listing.hostid, listing.price, " +
                    "startdate, enddate, streetno, streetname, city, province, postalcode, unitno from located join " +
                    "listing join reserved join address where address.addressid = located.addressid " +
                    "and listing.listid " + "= reserved.listid and " +
                    "listing.listid = located.listid and statusAvailable = false");
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
                reserveSet.add(reservationID);
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
        return reserveSet;
    }

    public static void showReservations(int username){
        printResTable(username);
        handleRenter(username);
    }

    public static void cancelReservation(int username) {
        HashSet<Integer> resSet = new HashSet<>();
        try {
            PreparedStatement s = con.prepareStatement("select reserved.reservationID, listing.listid, " +
                    "listing.hostid, listing.price, " +
                    "startdate, enddate, streetno, streetname, city, province, postalcode, unitno from located join " +
                    "listing join reserved join address where address.addressid = located.addressid " +
                    "and listing.listid " + "= reserved.listid and listing.listid = located.listid " +
                    "and statusAvailable = false and startDate > curdate()");
            ResultSet rs = s.executeQuery();
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s | %5$-10s | %6$-15s | %7$-15s " +
                    "| %8$-10s | %9$-10s | %10$-15s | %11$-10s | %12$-10s";
            System.out.println(String.format(format, "ReservationID", "ListID", "HostID", "Price", "Start Date",
                    "End Date", "House Number", "Street Name", "City", "Province", "Postal Code", "Unit No."));
            String under = "_";
            for (int i = 0; i < 150; i++) {
                under += "_";
            }
            System.out.println(under);

            while (rs.next()) {
                int reservationID = rs.getInt("reservationID");
                resSet.add(reservationID);
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
                System.out.println(String.format(format, reservationID + "", listID + "",
                        hostID + "", price + "", startDate, endDate,
                        streetNo + "", streetName, city, province, postalCode, unitNo + ""));

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int option;
        while (true) {
            try {
                System.out.println("Choose a reservation ID to cancel (-1 to exit):");
                option = scan.nextInt();
                if (option == -1){
                    handleRenter(username);
                }
                if (resSet.contains(option)) {
                    try{
                        //change status on reservation
                        PreparedStatement s = con.prepareStatement("update reserved set " +
                                "statusAvailable = true where reservationID = ?");
                        s.setInt(1, option);
                        s.executeUpdate();
                        System.out.println("Successfully cancelled.");
                        handleRenter(username);
                        return;
                    } catch (SQLException e){
                        System.out.println(e);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid.\n");
            }
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

        //3 options
        // not in reserved -> add to set
        // in reserved with no overlapping dates
        // in reserved with overlapping dates, but is cancelled

        //look through each listing
        //select * from listing where city = ? and listID NOT IN (select Reserved.listID from reserved);
        //UNION
        //select * from listing join reserved where city = ? and (reserved.startDate > ? or reserved.endDate < ?);
        //UNION
        //select * from listing join reserved where city = ? and (reserved.startDate <= ? and reserved.startDate >= ?) and statusAvailable = true;

        //select * from listing join reserved where city = ? and listing.listID = reserved.listID and NOT IN ( select * from listing join reserved where (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false);

        //display all the listings that are available
        //prompt choosing an option
    }

    public static void writeReview(int username) {

        HashSet<Integer> resSet = new HashSet<>();
        try {
            PreparedStatement s = con.prepareStatement("select reserved.reservationID, " +
                    "listing.listid, listing.hostid, listing.price, " +
                    "startdate, enddate, streetno, streetname, city, province, postalcode, unitno from located join " +
                    "listing join reserved join address where address.addressid = located.addressid " +
                    "and listing.listid " + "= reserved.listid and listing.listid = located.listid " +
                    "and statusAvailable = false and endDate < curdate()");
            ResultSet rs = s.executeQuery();
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s | %5$-10s | %6$-15s | %7$-15s " +
                    "| %8$-10s | %9$-10s | %10$-15s | %11$-10s | %12$-10s";
            System.out.println(String.format(format, "ReservationID", "ListID", "HostID",
                    "Price", "Start Date", "End Date",
                    "House Number", "Street Name", "City", "Province", "Postal Code", "Unit No."));
            String under = "_";
            for (int i = 0; i < 150; i++) {
                under += "_";
            }
            System.out.println(under);

            while (rs.next()) {
                int reservationID = rs.getInt("reservationID");
                resSet.add(reservationID);
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
                System.out.println(String.format(format, reservationID + "", listID + "", hostID + "", price +
                                "", startDate, endDate,
                        streetNo + "", streetName, city, province, postalCode, unitNo + ""));

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int option;
        while (true) {
            try {
                System.out.println("Choose a reservation ID to review (-1 to exit):");
                option = scan.nextInt();
                if (option == -1){
                    handleRenter(username);
                }
                int score;
                if (resSet.contains(option)) {
                    while (true){
                        System.out.println("Rate your stay from 1-5:");
                        try {
                            score = scan.nextInt();
                            break;
                        } catch (Exception e){
                            System.out.println("Must be an integer.");
                        }
                    }

                    System.out.println("Leave a review:");
                    String review = scan.next();


                    try{
                        //change status on reservation
                        PreparedStatement s = con.prepareStatement("update reserved set " +
                                "renterReview = ? where renterScore = ?");
                        s.setInt(2, score);
                        s.setString(1, review);
                        s.executeUpdate();
                        System.out.println("Successfully updated review.");
                        handleRenter(username);
                        return;
                    } catch (SQLException e){
                        System.out.println(e);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid.\n");
            }
        }
    }
}























