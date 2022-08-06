import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public static HashSet<Integer> printListingOptions(ResultSet rs){
        HashSet<Integer> listingSet = new HashSet<>();

        try {

            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s | %5$-10s | %6$-15s ";
            System.out.println(String.format(format, "Listing ID", "House Number", "Street Name",
                    "City", "Unit Number", "Price"));
            String under = "_";
            for (int i = 0; i < 90; i++){
                under += "_";
            }
            System.out.println(under);

            while (rs.next()){
                int listID = rs.getInt("listID");
                listingSet.add(listID);
//                int hostID = rs.getInt("hostID");
                int price = rs.getInt("price");
//                double longitude = rs.getDouble("longitude");
//                double latitude = rs.getDouble("latitude");
//                String listingType = rs.getString("listingType");

                //search into listing join located join reserved table for address
                int streetNo;
                String streetName;
                String city;
                int unitNo;
                PreparedStatement s1 = con.prepareStatement("select address.streetNo, address.streetName, " +
                        "address.city, address.unitNo from listing join located " +
                        "join address where listing.listID = located.listID and located.addressID = " +
                        "address.addressID and listing.listID = ?");
                s1.setInt(1, listID);
                ResultSet rs1 = s1.executeQuery();
                if (rs.next()){
                    streetNo = rs1.getInt("address.streetNo");
                    streetName = rs1.getString("address.streetName");
                    city = rs1.getString("address.city");
                    unitNo = rs1.getInt("address.unitNo");
                    System.out.println(String.format(format, listID+"", streetNo+"", streetName,
                            city, unitNo+"", price+""));
                }
            }
        } catch (Exception e){
            System.out.println("Unable to complete. Please try again.");
            Renter.handleRenter(username);
        }
        return listingSet;
    }

    public static void printAmenityList(HashSet<Integer> listingSet){
        int option;
        while (true){
            System.out.println("Choose listing to display amenities:");

            try {
                option = scan.nextInt();
                if (!listingSet.contains(option)){
                    System.out.println("Option not valid.\n");
                } else{
                    break;
                }
            } catch (Exception e){
                System.out.println("Invalid option. Must be integer.");
            }
        }

        try {
            PreparedStatement s = con.prepareStatement("select wifi, washer, ac, heating, tv, iron, kitchen, " +
                    "dryer, workspace, hairDryer, pool, parking, crib, grill, indoorFireplace, hotTub, evCharger, " +
                    "gym, breakfast, smoking, beachfront, waterfront, smokeAlarm, carbonMonoxideAlarm" +
                    "from Amenities join Provides join Listing where listing.listID = provides.listID " +
                    "and listing.listID = ? and amenities.amenityID = provides.amenityID");
            s.setInt(1, option);
            ResultSet rs = s.executeQuery();

            if (rs.next()){

                ArrayList<String> names = new ArrayList<>(List.of("wifi", "washer", "ac", "heating", "tv", "iron", "kitchen",
                        "dryer", "workspace", "hairDryer", "pool", "parking", "crib", "grill", "indoorFireplace", "hotTub", "evCharger",
                        "gym", "breakfast", "smoking", "beachfront", "waterfront", "smokeAlarm", "carbonMonoxideAlarm"));
                ArrayList<Boolean> bools = new ArrayList<>();
                boolean wifi = rs.getBoolean("wifi");
                bools.add(wifi);
                boolean washer = rs.getBoolean("washer");
                bools.add(washer);
                boolean ac = rs.getBoolean("ac");
                bools.add(ac);
                boolean heating = rs.getBoolean("heating");
                bools.add(heating);
                boolean tv = rs.getBoolean("tv");
                bools.add(tv);
                boolean iron = rs.getBoolean("iron");
                bools.add(iron);
                boolean kitchen = rs.getBoolean("kitchen");
                bools.add(kitchen);
                boolean dryer = rs.getBoolean("dryer");
                bools.add(dryer);
                boolean workspace = rs.getBoolean("workspace");
                bools.add(workspace);
                boolean hairDryer = rs.getBoolean("hairDryer");
                bools.add(hairDryer);
                boolean pool = rs.getBoolean("pool");
                bools.add(pool);
                boolean parking = rs.getBoolean("parking");
                bools.add(parking);
                boolean crib = rs.getBoolean("crib");
                bools.add(crib);
                boolean grill = rs.getBoolean("grill");
                bools.add(grill);
                boolean indoorFireplace = rs.getBoolean("indoorFireplace");
                bools.add(indoorFireplace);
                boolean hotTub = rs.getBoolean("hotTub");
                bools.add(hotTub);
                boolean evCharger = rs.getBoolean("evCharger");
                bools.add(evCharger);
                boolean gym = rs.getBoolean("gym");
                bools.add(gym);
                boolean breakfast = rs.getBoolean("breakfast");
                bools.add(breakfast);
                boolean smoking = rs.getBoolean("smoking");
                bools.add(smoking);
                boolean beachfront = rs.getBoolean("beachfront");
                bools.add(beachfront);
                boolean waterfront = rs.getBoolean("waterfront");
                bools.add(waterfront);
                boolean smokeAlarm = rs.getBoolean("smokeAlarm");
                bools.add(smokeAlarm);
                boolean carbonMonoxideAlarm = rs.getBoolean("carbonMonoxideAlarm");
                bools.add(carbonMonoxideAlarm);

                System.out.println();
                for (int i = 0; i < bools.size(); i++){
                    boolean hasAmen = bools.get(i);
                    if (hasAmen)
                        System.out.println(names.get(i) + ": Included");
                    else{
                        System.out.println(names.get(i) + ": Not Included");
                    }
                }
            }

        } catch (Exception e){
            System.out.println(e);
            printAmenityList(listingSet);
        }
    }

    public static void bookListing(HashSet<Integer> listingSet, String startDate, String endDate){
        int option;
        while (true){
            System.out.println("Choose listing to book:");

            try {
                option = scan.nextInt();
                if (!listingSet.contains(option)){
                    System.out.println("Option not valid.\n");
                } else{
                    break;
                }
            } catch (Exception e){
                System.out.println("Invalid option. Must be integer.");
            }
        }

        try {
            //get information needed from the listing id
            PreparedStatement s = con.prepareStatement("select * from listing where listing.listID = ?");
            s.setInt(1, option);
            ResultSet rs = s.executeQuery();
            int price = rs.getInt("price");
            int hostID = rs.getInt("hostID");

            //add an entry to reserved
            PreparedStatement s2 = con.prepareStatement("insert into reserved (hostID, renterID, listID, " +
                    "startDate, endDate, statusAvailable, price) values (?,?,?,?,?, false, ?)");
            s2.setInt(1, hostID);
            s2.setInt(2, username);
            s2.setInt(3, option);
            s2.setString(4, startDate);
            s2.setString(5, endDate);
            s2.setInt(6, price);

            int status = s2.executeUpdate();
            if (status == 1){
                System.out.println("Successfully booked!");
            } else{
                System.out.println("Unable to complete booking. Please try again.");
                bookListing(listingSet, startDate, endDate);
            }

        } catch (Exception e){
            System.out.println("Unable to complete booking. Please try again.");
            bookListing(listingSet, startDate, endDate);
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

        System.out.println("Country:");
        String country = scan.next();

        //3 options
        // not in reserved -> add to set
        // in reserved with no overlapping dates
        // in reserved with overlapping dates, but is cancelled


        try {
            PreparedStatement s = con.prepareStatement("select * from listing where city = ? and country = ? " +
                    "and listID NOT IN (select Reserved.listID from reserved) UNION" +
                    "select * from listing where city = ? and country = ? and " +
                    "NOT IN ( select listID, hostID, listingType, longitude, latitude, price from listing join " +
                    "reserved where listing.listID = reserved.listID and " +
                    "(reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false");
            s.setString(1, city);
            s.setString(2, country);
            s.setString(3, city);
            s.setString(4, country);
            s.setString(5, endDate);
            s.setString(6, startDate);
            ResultSet rs = s.executeQuery();
            HashSet<Integer> listingSet = printListingOptions(rs);


            //option to either view amenities or book the listing
            int option;
            while (true){
                System.out.println("\nChoose an option:\n" +
                        "(0) Exit to Menu \n" +
                        "(1) View Amenity List for Listing\n" +
                        "(2) Book Listing");

                try {
                    option = scan.nextInt();
                    if (option == 0){
                        handleRenter(username);
                        break;
                    } else if (option == 1){
                        printAmenityList(listingSet);
                        System.out.println();
                        printListingOptions(rs);
                    } else if (option == 2){
                        //create reservation
                        bookListing(listingSet, startDate, endDate);
                        break;
                    } else {
                        System.out.println("Invalid option.");
                    }
                } catch (Exception e){
                    System.out.println("Invalid type. Must be an integer.");
                }
            }





        } catch (Exception e){
            System.out.println(e);
        }

        //look through each listing

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























