import javax.xml.transform.Result;

import java.sql.*;
import java.util.Scanner;

public class ReportsQueries {

    static Connection con;
    static Scanner scan = new Scanner(System.in);

    static int user;

    public static void mainMenu(Connection connection, int username) {
        con = connection;
        user = username;

        //display all options
        System.out.println("Choose an option:\n" +
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
            if (option == 0){
                User.handleUserMainMenu(username);
            } else if (option == 1){

            } else if (option == 2){

            } else if (option == 3){
                searchByAddress();
            } else if (option == 4){

            } else if (option == 5){

            } else if (option == 6){
                numBookingsCity();
            } else if (option == 7){
                numBookingsCityPostal();
            } else if (option == 8){
                numListingsCountry();
            } else if (option == 9){
                numListingsCountryCity();
            } else if (option == 10){
                numListingsCountryCityPostal();
            } else if (option == 11){
                rankListingHostCountry();
            } else if (option == 12){
                rankListingHostCountryCity();
            } else if (option == 13){
                plus10percentListingsCountry();
            } else if (option == 14){
                plus10percentListingsCityCountry();
            } else if (option == 15){
                rankRenterBookings();
            } else if (option == 16){
                rankRenterBookingsCity();
            } else if (option == 17){
                rankHostCancel();
            } else if (option == 18){
                rankRenterCancel();
            } else if (option == 19){

            } else{
                System.out.println("Invalid option. Please try again.\n");
            }
            mainMenu(con, user);
        } catch (Exception e){
            System.out.println("Invalid option. Must be integer. Try again.\n");
            mainMenu(con, user);
        }

    }

    //queries
    //by distance

    public static void findListingByCoord(){
        Double lon, lat,distance = 0.0;

        while(true){
            System.out.println("Latitude:");
            lon = scan.nextDouble();
            System.out.println("Longitude:");
            lat = scan.nextDouble();
            if(lon >= -85 || lon <= 85 || lat >=-180 || lat <= 180 ) break;
            else System.out.println("Values out of range for longitude or latitude");
            System.out.println("Distance (km):");
            distance = scan.nextDouble();
            if(distance > 1) break;
            else System.out.println("Distance can't be negative");
        }
        String query = "SELECT * \n" +
                "FROM Listing \n" +
                "WHERE ST_Distance_Sphere(point(?,?), point(longitude,latitude))/1000 <= ?";
        try{
            PreparedStatement query1 = con.prepareStatement(query);
            query1.setDouble(1,lon);
            query1.setDouble(2,lat);
            query1.setDouble(3,distance);
            ResultSet rs = query1.executeQuery();
        }catch(Exception E){
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
        System.out.println("Street Name:");
        String streetName = scan.nextLine();
        System.out.println("City:");
        String city = scan.next();
        System.out.println("Province/State:");
        String prov = scan.next();
        System.out.println("Country:");
        String country = scan.next();
        System.out.println("Country:");
        String postalCode = scan.next();
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
            PreparedStatement s = con.prepareStatement("select listing.listID, listingType, price, streetNo, " +
                    "streetName, city, province, country, postalcode, unitNo from listing join located join " +
                    "address where " + "listing.listid = located.listid and address.addressid = located.addressid and " +
                    "streetNo = ? and streetName = ? and city = ? and province = ? and country = ? and unitNo = ? " +
                    "and postalCode = ?");
            s.setInt(1, houseNumber);
            s.setString(2, streetName);
            s.setString(3, city);
            s.setString(4, prov);
            s.setString(5, country);
            if (unitNumber == -1) {
                s.setNull(4, Types.INTEGER);
            } else {
                s.setInt(6, unitNumber);
            }
            s.setString(7, postalCode);

            ResultSet rs = s.executeQuery();

            String format = "%1$-8s| %2$-8s | %3$-10s | %4$-10s | %5$-10s | %6$-15s | %7$-15s " +
                    "| %8$-15s | %9$-15s | %10$-15s";
            System.out.println(String.format(format, "ListID", "ListingType", "Price",
                    "House Number", "Street Name", "City", "Province", "Country", "Postal Code", "Unit No."));
            String under = "_";
            for (int i = 0; i < 100; i++) {
                under += "_";
            }
            System.out.println(under);
            if (!rs.next()) {
                System.out.println("No Entries. \n");
                ReportsQueries.mainMenu(con, user);
            }

            while (rs.next()) {
                int listID = rs.getInt("listID");
                String listType = rs.getString("listingType");
                int price = rs.getInt("price");
                System.out.println(String.format(format, listID + "", listType, price + "",
                        houseNumber + "", streetName, city, prov, country, postalCode, unitNumber + ""));
            }


        } catch (Exception e) {
            System.out.println("Unable to complete query.");
        }
        mainMenu(con, user);

    }


    //return all listings available in certain date frame
// filter searches


//////////////////////////////////////////////////
    // reports


    // num of bookings in date range by city, country
    public static void numBookingsCity() {
        System.out.println("Starting date of reservation:");
        String startDate = scan.next();
        System.out.println("Ending date of reservation:");
        String endDate = scan.next();

        //choose city
        System.out.println("City:");
        String city = scan.next();

        System.out.println("Country:");
        String country = scan.next();

        try {
            PreparedStatement s = con.prepareStatement("select * from reserved where" +
                    "city = ? and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false and" +
                    "country = ?");
            s.setString(1, city);
            s.setString(2, endDate);
            s.setString(3, startDate);
            s.setString(4, country);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            System.out.println(String.format("Between %1 and %2 in %3, %4, there are %5 bookings.", startDate, endDate, city, country, counter));

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);

    }

    //num of bookings in date range by city, country, postal code
    public static void numBookingsCityPostal() {
        System.out.println("Starting date of reservation:");
        String startDate = scan.next();
        System.out.println("Ending date of reservation:");
        String endDate = scan.next();

        System.out.println("Postal Code:");
        String postal = scan.next();

        //choose city
        System.out.println("City:");
        String city = scan.next();

        System.out.println("Country:");
        String country = scan.next();


        try {
            PreparedStatement s = con.prepareStatement("select * from reserved where" +
                    "city = ? and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false and" +
                    "country = ? and postalCode = ?");
            s.setString(1, city);
            s.setString(2, endDate);
            s.setString(3, startDate);
            s.setString(4, country);
            s.setString(5, postal);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            System.out.println(String.format("Between %1 and %2 in %3, %4, %5, there are %6 bookings.", startDate, endDate, city, country, postal, counter));

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);

    }


    // total num of listings per country, city, postal code
    // country, country and city, country and city and postal code

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

    public static void numListingsCountry() {
        System.out.println("Country");
        String country = scan.next();

        int counter = getTotalListingsCountry(country);
        if (counter != -1) {
            System.out.println(String.format("There are %1 bookings in %2.\n", counter, country));
        }
        mainMenu(con, user);
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

    public static void numListingsCountryCity() {
        System.out.println("Country");
        String country = scan.next();

        System.out.println("City:");
        String city = scan.next();

        int counter = getTotalListingsCountryCity(country, city);
        if (counter != -1) {
            System.out.println(String.format("There are %1 bookings in %2, %3.\n", counter, city, country));
        }
        mainMenu(con, user);
    }

    public static void numListingsCountryCityPostal() {
        System.out.println("Country");
        String country = scan.next();

        System.out.println("City:");
        String city = scan.next();

        System.out.println("Postal Code:");
        String postal = scan.next();

        try {
            PreparedStatement s = con.prepareStatement("select * from listing join located join address" +
                    "where listing.listID = located.listID and located.addressID = address.addressID and " +
                    "address.country = ? and address.city = ? and address.postalCode = ?");
            s.setString(1, country);
            s.setString(2, city);
            s.setString(3, postal);
            ResultSet rs = s.executeQuery();

            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            System.out.println(String.format("There are %1 bookings in %2, %3, %4.", counter, postal, city, country));
        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        mainMenu(con, user);
    }


    //rank hosts by number of listings in country and city

    public static void rankListingHostCountry() {
        System.out.println("Country");
        String country = scan.next();

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

            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
                System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }

    public static void rankListingHostCountryCity() {
        System.out.println("Country");
        String country = scan.next();

        System.out.println("City:");
        String city = scan.next();

        try {
//            PreparedStatement s = con.prepareStatement("select listing.hostID, users.firstName, users.lastName, count(listing.listID) as total_list from Users join listing join located join address" +
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
            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
                System.out.println(String.format(format, hostID + "", firstName, lastName, count + ""));
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    // hosts with 10% or more of countries/cities total listings
    public static void plus10percentListingsCountry() {

        System.out.println("Country");
        String country = scan.next();
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
            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
            if (counter == 0){
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }

    public static void plus10percentListingsCityCountry() {
        System.out.println("City:");
        String city = scan.next();
        System.out.println("Country");
        String country = scan.next();
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
            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
            if (counter == 0){
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    //rank renters by number of bookings in a specific time period

    public static void rankRenterBookings(){
        System.out.println("Starting date of reservation:");
        String startDate = scan.next();
        System.out.println("Ending date of reservation:");
        String endDate = scan.next();

        try {
            PreparedStatement s = con.prepareStatement("select reserved.renterID, users.firstName, users.lastName, " +
                    "count(reservationID) as total_bookings from users join reserved where users.sin = reserved.renterID" +
                    "and (reserved.startDate <= ? and reserved.endDate >= ?) and statusAvailable = false");
            s.setString(1, endDate);
            s.setString(2, startDate);
            ResultSet rs = s.executeQuery();

            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);

    }


    //same for city - only interested in 2+ bookings

    public static void rankRenterBookingsCity(){
//        System.out.println("Starting date of reservation:");
//        String startDate = scan.next();
//        System.out.println("Ending date of reservation:");
//        String endDate = scan.next();

        System.out.println("City:");
        String city = scan.next();

        System.out.println("Country:");
        String country = scan.next();

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

            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con, user);
            }
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
                if (count >= 2){
                    System.out.println(String.format(format, renterID + "", firstName, lastName, count + ""));
                }
            }
            if (counter == 0){
                System.out.println("No Entries.\n");
            }

        } catch (Exception e) {
            System.out.println("Unable to complete.");
        }
        ReportsQueries.mainMenu(con, user);
    }


    // rank hosts by largest number of cancellations

    public static void rankHostCancel(){
        try {
            PreparedStatement s = con.prepareStatement("select reserved.hostID, users.firstName, users.lastName," +
                    "count(reservationID) as total from reserved join users where " +
                    "statusAvailable = true and users.sin = reserved.hostID and startDate >= 2021-08-01" +
                    "group by reserved.hostID order by total DESC");
            ResultSet rs = s.executeQuery();


            if (!rs.next()){
                System.out.println("No Entries.\n");
                mainMenu(con, user);
            }
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
            }



        } catch (Exception e){
            System.out.println("Unable to complete query.");
            mainMenu(con, user);
        }
    }


    // rank renters by largest number of cancellations

    public static void rankRenterCancel(){
        try {
            PreparedStatement s = con.prepareStatement("select reserved.renterID, users.firstName, users.lastName," +
                    "count(reservationID) as total from reserved join users where " +
                    "statusAvailable = true and users.sin = reserved.renterID and startDate >= 2021-08-01" +
                    "group by reserved.renterID order by total DESC");
            ResultSet rs = s.executeQuery();


            if (!rs.next()){
                System.out.println("No Entries.\n");
                mainMenu(con, user);
            }
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
            }



        } catch (Exception e){
            System.out.println("Unable to complete query.");
            mainMenu(con, user);
        }
    }



}
