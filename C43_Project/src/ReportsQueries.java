import javax.xml.transform.Result;

import java.sql.*;
import java.util.Scanner;

public class ReportsQueries {

    static Connection con;
    static Scanner scan = new Scanner(System.in);

    public static void mainMenu(Connection connection) {
        con = connection;

        //display all options
        //take in user input and redirect

    }

    //queries
    //by distance
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
                ReportsQueries.mainMenu(con);
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
        mainMenu(con);

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
        ReportsQueries.mainMenu(con);

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
        ReportsQueries.mainMenu(con);

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
        mainMenu(con);
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
        mainMenu(con);
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
        mainMenu(con);
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

            int counter = 0;
            if (!rs.next()) {
                System.out.println("No Entries.\n");
                ReportsQueries.mainMenu(con);
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
        ReportsQueries.mainMenu(con);
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
                ReportsQueries.mainMenu(con);
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
        ReportsQueries.mainMenu(con);
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
                ReportsQueries.mainMenu(con);
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
        ReportsQueries.mainMenu(con);
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
                ReportsQueries.mainMenu(con);
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
        ReportsQueries.mainMenu(con);
    }


    //rank renters by number of bookings in a specific time period
    //same for city - only interested in 2+ bookings
    // rank hosts/renters by largest number of cancellations
    public static void distanceByLatAndLong(){
        String
        PreparedStatement query1 = con.prepareStatement(query);
    }

}
