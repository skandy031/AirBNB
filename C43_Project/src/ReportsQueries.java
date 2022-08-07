import javax.xml.transform.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ReportsQueries {

    static Connection con;
    static Scanner scan = new Scanner(System.in);

    static int user;

    public static void mainMenu(Connection connection, int username) {
        con = connection;
        user = username;

        //display all options
        System.out.println("\nChoose an option:\n" +
                "(0) Exit\n" +
                "(1) Search by Distance\n" +
                "(2) Search by Postal Code\n" +
                "(3) Search by Address\n" +
                "(4) Search by Date Range\n" +
                "(5) Filtered Search\n" +
                "(6) Number of Bookings by City in Date Range\n" +
                "(7) Number of Bookings by City, Zip in Date Range\n" +
                "(8) Number of Listings by Country\n" +
                "(9) Number of Listings by City, Country\n" +
                "(10) Number of Listings by Zip, City, Country\n" +
                "(11) Rank Hosts by Number of Listings - Country\n" +
                "(12) Rank Hosts by Number of Listings - City\n" +
                "(13) Find Potential Commercial Hosts - Country\n" +
                "(14) Find Potential Commercial Hosts - City\n" +
                "(15) Rank Renter by Number of Bookings \n" +
                "(16) Rank Renter by Number of Bookings - City\n" +
                "(17) Rank Hosts by Cancellations in Past Year\n" +
                "(18) Rank Renters by Cancellations in Past Year\n" +
                "(19) Listing Noun Phrases");
        //take in user input and redirect
        int option;
        try {
            option = scan.nextInt();
            if (option == 0) {
                User.handleUserMainMenu(username, con);
            } else if (option == 1) {
                findListingByCoord();
            } else if (option == 2) {

            } else if (option == 3) {
                searchByAddress();
            } else if (option == 4) {
                searchByTime();
            } else if (option == 5) {
                filteredSearch();
            } else if (option == 6) {
                numBookingsCity();
            } else if (option == 7) {
                numBookingsCityPostal();
            } else if (option == 8) {
                numListingsCountry();
            } else if (option == 9) {
                numListingsCountryCity();
            } else if (option == 10) {
                numListingsCountryCityPostal();
            } else if (option == 11) {
                rankListingHostCountry();
            } else if (option == 12) {
                rankListingHostCountryCity();
            } else if (option == 13) {
                plus10percentListingsCountry();
            } else if (option == 14) {
                plus10percentListingsCityCountry();
            } else if (option == 15) {
                rankRenterBookings();
            } else if (option == 16) {
                rankRenterBookingsCity();
            } else if (option == 17) {
                rankHostCancel();
            } else if (option == 18) {
                rankRenterCancel();
            } else if (option == 19) {

            } else {
                System.out.println("Invalid option. Please try again.\n");
            }
            mainMenu(con, user);
        } catch (Exception e) {
            System.out.println("Invalid option. Must be integer. Try again.\n");
            mainMenu(con, user);
        }

    }

    //queries
    //by distance

    public static void findListingByCoord() {
        Double lon, lat, distance = 0.0;

        while (true) {
            System.out.println("Latitude:");
            lon = scan.nextDouble();
            System.out.println("Longitude:");
            lat = scan.nextDouble();
            if (lon >= -85 || lon <= 85 || lat >= -180 || lat <= 180) break;
            else System.out.println("Values out of range for longitude or latitude");
            System.out.println("Distance (km):");
            distance = scan.nextDouble();
            if (distance > 1) break;
            else System.out.println("Distance can't be negative");
        }
        String query = "SELECT * \n" +
                "FROM Listing \n" +
                "WHERE ST_Distance_Sphere(point(?,?), point(longitude,latitude))/1000 <= ?";
        try {
            PreparedStatement query1 = con.prepareStatement(query);
            query1.setDouble(1, lon);
            query1.setDouble(2, lat);
            query1.setDouble(3, distance);
            ResultSet rs = query1.executeQuery();
        } catch (Exception E) {
            System.out.println(E);
        }


    }


    //add search mechanics

    // query by address
    public static void searchByAddress() {
        System.out.println("House Number:");
        int houseNumber;
        while (true) {
            try {
                houseNumber = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid. Must be integer.");
            }
        }
//        scan = null;
        scan = new Scanner(System.in);
        System.out.println("Street Name:");
        String streetName = scan.nextLine();
        System.out.println("City:");
        String city = scan.nextLine();
        System.out.println("Province/State:");
        String prov = scan.nextLine();
        System.out.println("Country:");
        String country = scan.nextLine();
        System.out.println("Postal Code:");
        String postalCode = scan.nextLine();


        System.out.println("Unit Number (If no Unit Number, type -1):");
        int unitNumber;
        while (true) {
            try {
                unitNumber = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid. Must be integer.");
            }
        }

        try {
            String query = "select listing.listID, listingType, price, streetNo, " +
                    "streetName, city, province, country, postalcode, unitNo from listing join located join " +
                    "address where " + "listing.listid = located.listid and address.addressid = located.addressid and " +
                    "streetNo = ? and streetName = ? and city = ? and province = ? and country = ? " +
                    "and postalCode = ? and unitNo = ";

            PreparedStatement s;
            if (unitNumber < 0) {
                query += "null";
                s = con.prepareStatement(query);

            } else {
                query += "?";
                s = con.prepareStatement(query);
                s.setInt(7, unitNumber);
            }
            s.setInt(1, houseNumber);
            s.setString(2, streetName);
            s.setString(3, city);
            s.setString(4, prov);
            s.setString(5, country);
            s.setString(6, postalCode);



            ResultSet rs = s.executeQuery();

            String format = "%1$-8s| %2$-20s | %3$-10s | %4$-15s | %5$-10s | %6$-10s | %7$-15s " +
                    "| %8$-10s | %9$-15s | %10$-15s";
            System.out.println(String.format(format, "ListID", "ListingType", "Price",
                    "House Number", "Street Name", "City", "Province", "Country", "Postal Code", "Unit No."));
            String under = "_";
            for (int i = 0; i < 150; i++) {
                under += "_";
            }
            System.out.println(under);

            int counter = 0;
            while (rs.next()) {
                int listID = rs.getInt("listID");
                String listType = rs.getString("listingType");
                int price = rs.getInt("price");
                System.out.println(String.format(format, listID + "", listType, price + "",
                        houseNumber + "", streetName, city, prov, country, postalCode, unitNumber + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries. \n");
                ReportsQueries.mainMenu(con, user);
            }


        } catch (Exception e) {
            System.out.println("Unable to complete query.");
        }
        mainMenu(con, user);

    }


    //return all listings available in certain date frame

    public static void searchByTime(){
        System.out.println("Starting date:");
        String startDate = scan.nextLine();
        System.out.println("Ending date:");
        String endDate = scan.nextLine();
        String query = "((select listing.listid from listing where listid not in " +
                "(select listid from reserved)) union (select listing.listID from listing " +
                "join reserved where listing.listid = reserved.listid and listing.listid not in " +
                "(select listing.listID from listing join reserved where reserved.listid = listing.listid " +
                "and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false)))";

        try {
            PreparedStatement s = con.prepareStatement(query);
            s.setString(1, endDate);
            s.setString(2, startDate);
            ResultSet rs = s.executeQuery();

            //print out all the listings
            printListingOptions(rs);
            //send back to the menu
            mainMenu(con, user);

        } catch (Exception e){
            System.out.println("Unable to complete. Please try again.");
            mainMenu(con, user);
        }
    }


    // filter searches
    private static void printListingOptions(ResultSet rs){
//        HashSet<Integer> listingSet = new HashSet<>();

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
//                listingSet.add(listID);
//                int hostID = rs.getInt("hostID");
//                int price = rs.getInt("price");
//                double longitude = rs.getDouble("longitude");
//                double latitude = rs.getDouble("latitude");
//                String listingType = rs.getString("listingType");

                //search into listing join located join reserved table for address
                int streetNo, price;
                String streetName;
                String city;
                int unitNo;
                PreparedStatement s1 = con.prepareStatement("select address.streetNo, address.streetName, " +
                        "address.city, address.unitNo, price from listing join located " +
                        "join address where listing.listID = located.listID and located.addressID = " +
                        "address.addressID and listing.listID = ?");
                s1.setInt(1, listID);
                ResultSet rs1 = s1.executeQuery();
                if (rs.next()){
                    streetNo = rs1.getInt("address.streetNo");
                    streetName = rs1.getString("address.streetName");
                    city = rs1.getString("address.city");
                    unitNo = rs1.getInt("address.unitNo");
                    price = rs1.getInt("price");
                    System.out.println(String.format(format, listID+"", streetNo+"", streetName,
                            city, unitNo+"", price+""));
                }
            }
        } catch (Exception e){
            System.out.println("Unable to complete. Please try again.");
            mainMenu(con, user);
        }
    }

    public static void filteredSearch() {

        String query = "select listing.listid from listing join located join address join provides join amenities " +
                "join reserved where listing.listid = reserved.listid and " +
                "listing.listid = located.listid and address.addressid = located.addressid and listing.listid = " +
                "provides.listid and provides.amenityID = amenities.amenityID";

        //search by country, city, postal code
        System.out.println("Search by country (y/n):");
        String option = scan.nextLine();
        if (option.toLowerCase() == "y") {
            System.out.println("Country:");
            String country = scan.nextLine();
            query += " and address.country = '" + country + "'";

            System.out.println("Add city to search (y/n):");
            option = scan.nextLine();
            if (option.toLowerCase() == "y") {
                System.out.println("Country:");
                String city = scan.nextLine();
                query += " and address.city = '" + city + "'";

                System.out.println("Add postal code to search (y/n):");
                option = scan.nextLine();
                if (option.toLowerCase() == "y") {
                    System.out.println("Postal Code:");
                    String postal = scan.nextLine();
                    query += " and address.postalCode = '" + postal + "'";

                    System.out.println("Add street name to search (y/n):");
                    option = scan.nextLine();
                    if (option.toLowerCase() == "y"){
                        System.out.println("Street Name:");
                        String streetName = scan.nextLine();
                        query += " and address.streetName = '" + streetName + "'";

                        System.out.println("Add house number to search (y/n):");
                        option = scan.nextLine();
                        if (option.toLowerCase() == "y"){
                            int houseNum;
                            while (true){
                                try {
                                    System.out.println("House Number:");
                                    houseNum = scan.nextInt();
                                    break;
                                } catch (Exception e){
                                    System.out.println("Must be an integer. Try again.");
                                }

                            }
                            query += " and address.streetNo = " + houseNum;
                        }

                    }
                }
            }

        }

        //search by price
        System.out.println("Add price range to search (y/n):");
        option = scan.nextLine();
        if (option == "y") {
            while (true) {
                int lower, upper;
                try {
                    System.out.println("Lower bound of price:");
                    lower = scan.nextInt();
                    System.out.println("Upper bound of price:");
                    upper = scan.nextInt();
                    query += " and listing.price > " + lower + " and listing.price < " + upper;

                } catch (Exception e) {
                    System.out.println("Must be integer. ");
                }
            }
        }

        //search by listing type
        System.out.println("Search by listing type (y/n):");
        option = scan.nextLine();
        if (option == "y") {
            System.out.println("Listing Type:");
            String listType = scan.nextLine();
            query += " and listing.listingType = '" + listType + "'";
        }


        //search by amenities
        System.out.println("Search by amenities:");
        option = scan.nextLine();
        if (option.toLowerCase() == "y"){
            ArrayList<Integer> choices = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>(List.of("wifi", "washer", "ac", "heating", "tv", "iron", "kitchen",
                    "dryer", "workspace", "hairDryer", "pool", "parking", "crib", "grill", "indoorFireplace", "hotTub", "evCharger",
                    "gym", "breakfast", "smoking", "beachfront", "waterfront", "smokeAlarm", "carbonMonoxideAlarm"));

            System.out.println("Choose Amenities:");
            System.out.println("(1) Wifi, (2) Washer, (3) Air Conditioning, (4) Heating,\n" +
                    "(5) Television, (6) Iron, (7) Kitchen, (8) Dryer, (9) Workspace,\n" +
                    "(10) Hair Dryer, (11) Pool, (12) Parking, (13) Crib, (14) Grill,\n" +
                    "(15) Indoor Fireplace, (16) Hot Tub, (17) EV Charger, (18) Gym,\n" +
                    "(19) Breakfast, (20) Smoking, (21) Beachfront, (22) Waterfront\n" +
                    "(23) Smoke Alarm, (24) Carbonmononxide Alarm");
            System.out.println("Type 0 to stop");
            int bre = 0;
            while (bre == 0) {
                System.out.println("Amenities Chosen: " + choices);
                Integer option_amen = scan.nextInt();
                if (option_amen == 0) {
                    bre = 1;
                } else {
                    if (!choices.contains(option_amen)) {
                        choices.add(option_amen);
                    } else {
                        System.out.println("Option already selected.");
                    }
                }
            }

            for (int i = 0; i < 24; i ++){
                if (choices.contains(i))
                    query += " and " + names.get(i) + "= true";
                else query += " and " + names.get(i) + "= false";
            }
        }



        //search by time frame
        System.out.println("Add time frame availability to search (y/n):");
        option = scan.nextLine();
        if (option.toLowerCase() == "y"){
            query = "(" + query + ")";
            System.out.println("Starting date:");
            String startDate = scan.nextLine();
            System.out.println("Ending date:");
            String endDate = scan.nextLine();
            query += " intersects ((select listing.listid from listing where listid not in " +
                    "(select listid from reserved)) union (select listing.listID from listing " +
                    "join reserved where listing.listid = reserved.listid and listing.listid not in " +
                    "(select listing.listID from listing join reserved where reserved.listid = listing.listid " +
                    "and (reserved.startDate <= '" + endDate + "' and reserved.endDate >= '" + startDate + "' " +
                    ") and statusAvailable = false)))";
        }

        //all filters are completed
        //now have a list of listing id's
        try {
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            //print out all the listings
            printListingOptions(rs);
            //send back to the menu
            mainMenu(con, user);

        } catch (Exception e){
            System.out.println("Unable to complete. Please try again.");
            mainMenu(con, user);
        }
    }


//////////////////////////////////////////////////
    // reports


    // num of bookings in date range by city, country
    public static void numBookingsCity() {
        System.out.println("Starting date of reservation:");
        String startDate = scan.nextLine();
        System.out.println("Ending date of reservation:");
        String endDate = scan.nextLine();


        try {
            PreparedStatement s = con.prepareStatement("select city, country" +
                    "count(reserved.listid) as total from reserved join located join address where " +
                    "reserved.listid = located.listid and located.addressid = address.addressid and" +
                    "(reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false " +
                    "group by city, country");

            s.setString(1, endDate);
            s.setString(2, startDate);
            ResultSet rs = s.executeQuery();

            //print out all the listings
            int counter = 0;
            String format = "%1$-8s| %2$-10s | %3$-10s";
            System.out.println(String.format(format, "City", "Country", "Count"));
            String under = "_";
            for (int i = 0; i < 30; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                String city = rs.getString("city");
                String country = rs.getString("country");
                int totalCount = rs.getInt("total");
                System.out.println(String.format(format, city, country, totalCount+""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }


        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }

    //num of bookings in date range by city, country, postal code
    public static void numBookingsCityPostal() {
        System.out.println("Starting date of reservation:");
        String startDate = scan.nextLine();
        System.out.println("Ending date of reservation:");
        String endDate = scan.nextLine();


        try {
            PreparedStatement s = con.prepareStatement("select city, country, postalCode" +
                    "count(reserved.listid) as total from reserved join located join address where " +
                    "reserved.listid = located.listid and located.addressid = address.addressid and" +
                    "(reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false " +
                    "group by city, country, postalCode");

            s.setString(1, endDate);
            s.setString(2, startDate);
            ResultSet rs = s.executeQuery();

            //print out all the listings
            int counter = 0;
            String format = "%1$-8s| %2$-10s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "City", "Country", "Postal Code", "Count"));
            String under = "_";
            for (int i = 0; i < 30; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                String city = rs.getString("city");
                String country = rs.getString("country");
                String postal = rs.getString("postalCode");
                int totalCount = rs.getInt("total");
                System.out.println(String.format(format, city, country, postal, totalCount+""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }


        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    // total num of listings per country, city, postal code
    // country, country and city, country and city and postal code

    public static void numListingsCountry() {
        try {
            PreparedStatement s = con.prepareStatement("select country, count(listing.listid) as total from " +
                    "listing join located join address where listing.listid = located.listid and " +
                    "located.addressid = address.addressid group by country");
            ResultSet rs = s.executeQuery();

            //print out all the listings
            int counter = 0;
            String format = "%1$-8s| %2$-10s";
            System.out.println(String.format(format, "Country", "Count"));
            String under = "_";
            for (int i = 0; i < 30; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                String country = rs.getString("country");
                int totalCount = rs.getInt("total");
                System.out.println(String.format(format, country, totalCount+""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e){
            System.out.println("Unable to complete.");
            mainMenu(con, user);
        }
    }

    public static void numListingsCountryCity() {
        try {
            PreparedStatement s = con.prepareStatement("select country, city, count(listing.listid) as total from " +
                    "listing join located join address where listing.listid = located.listid and " +
                    "located.addressid = address.addressid group by country, city");
            ResultSet rs = s.executeQuery();

            //print out all the listings

            String format = "%1$-8s| %2$-10s | %3$-10s";
            System.out.println(String.format(format, "Country", "City", "Count"));
            String under = "_";
            for (int i = 0; i < 40; i++) {
                under += "_";
            }
            System.out.println(under);
            int counter = 0;
            while (rs.next()) {
                String country = rs.getString("country");
                String city = rs.getString("city");
                int totalCount = rs.getInt("total");
                System.out.println(String.format(format, country, city, totalCount+""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e){
            System.out.println("Unable to complete.");
            mainMenu(con, user);
        }
    }

    public static void numListingsCountryCityPostal() {
        try {
            PreparedStatement s = con.prepareStatement("select country, city, postalCode, count(listing.listid) as total from " +
                    "listing join located join address where listing.listid = located.listid and " +
                    "located.addressid = address.addressid group by country, city, postalCode");
            ResultSet rs = s.executeQuery();

            //print out all the listings
            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
            String format = "%1$-8s| %2$-10s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Country", "City", "Postal Code", "Count"));
            String under = "_";
            for (int i = 0; i < 50; i++) {
                under += "_";
            }
            int counter = 0;
            System.out.println(under);
            while (rs.next()) {
                String country = rs.getString("country");
                String city = rs.getString("city");
                String postalCode = rs.getString("postalCode");
                int totalCount = rs.getInt("total");
                System.out.println(String.format(format, country, city, postalCode, totalCount+""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e){
            System.out.println("Unable to complete.");
            mainMenu(con, user);
        }
    }


    //rank hosts by number of listings in country and city

    public static void rankListingHostCountry() {
        System.out.println("Country:");
        String country = scan.nextLine();

        try {
//            PreparedStatement s = con.prepareStatement("select listing.hostID, users.firstName, users.lastName, " +
//                    "count(listing.listID) as total_list from Users join listing join located join address" +
//                    "where listing.hostID = users.sin and listing.listID = located.listID and " +
//                    "located.addressID = address.addressID and address.country = ?" +
//                    "group by listing.hostID order by total_list DESC");
            PreparedStatement s = con.prepareStatement("select users.sin, users.firstName, users.lastName, " +
                    "count(listing.listID) as total_list from Users join owns join listing join located join address " +
                    "where owns.listID = listing.listID and owns.hostID = users.sin and " +
                    "listing.listID = located.listID and " +
                    "located.addressID = address.addressID and address.country = ? " +
                    "group by users.sin, firstName, lastName order by total_list DESC");
            s.setString(1, country);
            ResultSet rs = s.executeQuery();

            String format = "%1$-8s| %2$-20s | %3$-20s | %4$-10s";
            System.out.println(String.format(format, "Host ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 40; i++) {
                under += "_";
            }
            System.out.println(under);
            int counter = 0;
            while (rs.next()) {
                int hostID = rs.getInt("users.sin");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_list");
                System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }

    public static void rankListingHostCountryCity() {
        System.out.println("Country");
        String country = scan.nextLine();

        System.out.println("City:");
        String city = scan.nextLine();

        try {
//            PreparedStatement s = con.prepareStatement("select listing.hostID, users.firstName, users.lastName, count(listing.listID) as total_list from Users join listing join located join address" +
//                    "where listing.hostID = users.sin and listing.listID = located.listID and " +
//                    "located.addressID = address.addressID and address.country = ? and address.city = ?" +
//                    "group by listing.hostID order by total_list DESC");
            PreparedStatement s = con.prepareStatement("select sin, users.firstName, users.lastName, " +
                    "count(listing.listID) as total_list from Users join owns join listing join located join address " +
                    "where owns.listID = listing.listID and owns.hostID = users.sin and " +
                    "listing.listID = located.listID and " +
                    "located.addressID = address.addressID and address.country = ? and address.city = ? " +
                    "group by sin, firstName, lastName order by total_list DESC");
            s.setString(1, country);
            s.setString(2, city);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            String format = "%1$-8s| %2$-20s | %3$-20s | %4$-10s";
            System.out.println(String.format(format, "Host ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int hostID = rs.getInt("sin");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_list");
                System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    //helper for getting total listings in a country
    public static int getTotalListingsCountry(String country) {
        try {
            PreparedStatement s = con.prepareStatement("select * from listing join located join address" +
                    "where listing.listID = located.listID and located.addressID = address.addressID and " +
                    "address.country = ?");
            s.setString(1, country);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            return counter;
        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        return -1;
    }

    //helper for getting total listings in a city+country
    public static int getTotalListingsCountryCity(String country, String city) {
        try {
            PreparedStatement s = con.prepareStatement("select * from listing join located join address" +
                    "where listing.listID = located.listID and located.addressID = address.addressID and " +
                    "address.country = ? and address.city = ?");
            s.setString(1, country);
            s.setString(2, city);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            return counter;
        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        return -1;
    }


    // hosts with 10% or more of countries/cities total listings
    public static void plus10percentListingsCountry() {

        System.out.println("Country");
        String country = scan.nextLine();
        int totalListings = getTotalListingsCountry(country);

        int tenPercList = totalListings / 10;

        try {
//            PreparedStatement s = con.prepareStatement("select listing.hostID, users.firstName, users.lastName, " +
//                    "count(listing.listID) as total_list from Users join listing join located join address" +
//                    "where listing.hostID = users.sin and listing.listID = located.listID and " +
//                    "located.addressID = address.addressID and address.country = ?" +
//                    "group by listing.hostID order by total_list DESC");
            PreparedStatement s = con.prepareStatement("select owns.hostID, users.firstName, users.lastName, " +
                    "count(listing.listID) as total_list from Users join owns join listing join located join address" +
                    "where owns.listID = listing.listID and owns.hostID = users.sin and " +
                    "listing.listID = located.listID and " +
                    "located.addressID = address.addressID and address.country = ?" +
                    "group by listing.hostID order by total_list DESC");
            s.setString(1, country);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Host ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int hostID = rs.getInt("listing.hostID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_list");
                if (count >= tenPercList) {
                    counter++;
                    System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
                }
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }

    public static void plus10percentListingsCityCountry() {
        System.out.println("City:");
        String city = scan.nextLine();
        System.out.println("Country");
        String country = scan.nextLine();
        int totalListings = getTotalListingsCountryCity(country, city);

        int tenPercList = totalListings / 10;

        try {
//            PreparedStatement s = con.prepareStatement("select listing.hostID, users.firstName, users.lastName, " +
//                    "count(listing.listID) as total_list from Users join listing join located join address" +
//                    "where listing.hostID = users.sin and listing.listID = located.listID and " +
//                    "located.addressID = address.addressID and address.country = ? and address.city = ?" +
//                    "group by listing.hostID order by total_list DESC");
            PreparedStatement s = con.prepareStatement("select owns.hostID, users.firstName, users.lastName, " +
                    "count(listing.listID) as total_list from Users join owns join listing join located join address" +
                    "where owns.listID = listing.listID and owns.hostID = users.sin and " +
                    "listing.listID = located.listID and " +
                    "located.addressID = address.addressID and address.country = ? and address.city = ?" +
                    "group by listing.hostID order by total_list DESC");
            s.setString(1, country);
            s.setString(2, city);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Host ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int hostID = rs.getInt("listing.hostID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_list");
                if (count >= tenPercList) {
                    counter++;
                    System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
                }
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    //rank renters by number of bookings in a specific time period

    public static void rankRenterBookings() {
        System.out.println("Starting date of reservation:");
        String startDate = scan.nextLine();
        System.out.println("Ending date of reservation:");
        String endDate = scan.nextLine();

        try {
            PreparedStatement s = con.prepareStatement("select reserved.renterID, users.firstName, users.lastName, " +
                    "count(reservationID) as total_bookings from users join reserved where users.sin = reserved.renterID" +
                    "and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false");
            s.setString(1, endDate);
            s.setString(2, startDate);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Renter ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int renterID = rs.getInt("reserved.renterID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_bookings");
                System.out.println(String.format(format, renterID + "", firstName, lastName, count + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);

    }


    //same for city - only interested in 2+ bookings

    public static void rankRenterBookingsCity() {
//        System.out.println("Starting date of reservation:");
//        String startDate = scan.nextLine();
//        System.out.println("Ending date of reservation:");
//        String endDate = scan.nextLine();

        System.out.println("City:");
        String city = scan.nextLine();

        System.out.println("Country:");
        String country = scan.nextLine();

        try {
            PreparedStatement s = con.prepareStatement("select reserved.renterID, users.firstName, users.lastName, " +
                    "count(reservationID) as total_bookings from users join reserved join located join address " +
                    "where users.sin = reserved.renterID and reserved.listID = located.listID and " +
                    "address.addressID = located.addressID" +
                    "and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false " +
                    "and address.city = ? and address.country = ?");
            s.setString(1, "2022-08-08");
            s.setString(2, "2021-08-08");
            s.setString(3, city);
            s.setString(4, country);
            ResultSet rs = s.executeQuery();


            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Renter ID", "First Name", "Last Name", "Count"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            int counter = 0;
            while (rs.next()) {
                int renterID = rs.getInt("reserved.renterID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total_bookings");
                if (count >= 2) {
                    System.out.println(String.format(format, renterID + "", firstName, lastName, count + ""));
                    counter++;
                }
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    // rank hosts by largest number of cancellations

    public static void rankHostCancel() {
        try {
            PreparedStatement s = con.prepareStatement("select reserved.hostID, users.firstName, users.lastName," +
                    "count(reservationID) as total from reserved join users where " +
                    "statusAvailable = true and users.sin = reserved.hostID and startDate >= 2021-08-01" +
                    "group by reserved.hostID order by total DESC");
            ResultSet rs = s.executeQuery();


            int counter = 0;
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Host ID", "First Name", "Last Name", "Total Cancellations"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int hostID = rs.getInt("reserved.hostID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total");
                System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }


        } catch (Exception e) {
            System.out.println("Unable to complete query.");
            mainMenu(con, user);
        }
    }


    // rank renters by largest number of cancellations

    public static void rankRenterCancel() {
        try {
            PreparedStatement s = con.prepareStatement("select reserved.renterID, users.firstName, users.lastName," +
                    "count(reservationID) as total from reserved join users where " +
                    "statusAvailable = true and users.sin = reserved.renterID and startDate >= 2021-08-01" +
                    "group by reserved.renterID order by total DESC");
            ResultSet rs = s.executeQuery();


            int counter = 0;
            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s";
            System.out.println(String.format(format, "Renter ID", "First Name", "Last Name", "Total Cancellations"));
            String under = "_";
            for (int i = 0; i < 60; i++) {
                under += "_";
            }
            System.out.println(under);
            while (rs.next()) {
                int renterID = rs.getInt("reserved.renterID");
                String firstName = rs.getString("users.firstName");
                String lastName = rs.getString("users.lastName");
                int count = rs.getInt("total");
                System.out.println(String.format(format, renterID + "", firstName, lastName, count + ""));
                counter++;
            }
            if (counter == 0) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }


        } catch (Exception e) {
            System.out.println("Unable to complete query.");
            mainMenu(con, user);
        }
    }


}
