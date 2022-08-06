import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class Host {
    static Scanner scan = new Scanner(System.in);
    static Connection con = null;
    static Integer user = null;

//    public static void handleHostLogin(Connection conn)  {
//        System.out.println("Enter username:");
//        Integer username = scan.nextInt();
//        System.out.println("Enter password:");
//        String password = scan.next();
//        con = conn;
//        try{
//            //Query checks Users database
//            PreparedStatement query1 = con.prepareStatement("SELECT sin,password FROM Users WHERE sin = ? AND password = ?");
//            query1.setInt(1, username);
//            query1.setString(2, password);
//            ResultSet rs1 = query1.executeQuery();
//
//            //Query checks Hosts database
//            PreparedStatement query2 = con.prepareStatement("SELECT * FROM Hosts WHERE hostID = ?");
//            query2.setInt(1, username);
//            ResultSet rs2 = query2.executeQuery();
//
//            if(rs1.next() && rs2.next()){
//                System.out.println("Proper");
//                con = conn;
//                user = username;
//                handleHost(user);
//            }
//            else{
//                System.out.println("Incorrect Credentials\n");
//                handleHostLogin(con);
//
//            }
//        }catch(Exception e) {
//            System.out.println(e);
//        }
//        //check to see if this matches in database
//        //if it does not match
//        //print out statement
//        //call function again
//        //if it does match
//        //send to handleHost(user)
//        scan.close();
//    }


    public static void createListing(){
        String query = "";
        ResultSet rs;
        PreparedStatement query1;
        Double lon,lat, price = 0.0;
        Integer sNo,unitNo, opt = 0;
        List x = new ArrayList<>();
        Hashtable<Integer, String> listingTypes  = new Hashtable<Integer, String>(){
            {
            put(1,"Full house");
            put(2, "Apartment");
            put(3, "Room");}};
        String sName,city,province, country, code ="";
        try {
            while(true) {
                System.out.println("Listing Type:");
                System.out.println("  (1) Full house");
                System.out.println("  (2) Apartment");
                System.out.println("  (3) Room");
                opt = scan.nextInt();
                if (opt != 1 || opt != 2 || opt != 3) {
                    System.out.println("Invalid Option");
                    continue;
                }
                System.out.println("Longitude:");
                lon = scan.nextDouble();
                if (lon.doubleValue() > 180 || lon.doubleValue() < -180){
                    System.out.println("Invalid Option");
                    continue;
                }
                System.out.println("Latitude:");
                lat = scan.nextDouble();
                if (lat.doubleValue() > 90 || lat.doubleValue() < -90){
                    System.out.println("Invalid Option");
                    continue;
                }
                System.out.println("Price");
                price = scan.nextDouble();
                // Insert into Database
                //            System.out.println(user);

                System.out.println("Street Number:");
                sNo = scan.nextInt();
                System.out.println("Street Name:");
                 sName = scan.next();
                System.out.println("City:");
                 city = scan.next();
                System.out.println("Province:");
                 province = scan.next();
                System.out.println("Country:");
                country = scan.next();
                System.out.println("Postal Code:");
                 code = scan.next();

                if(code.length() >=6){
                    System.out.println("Postal/Zip code too long");
                    continue;
                }
                System.out.println("Unit Number:");
                unitNo = scan.nextInt();

                //Insert into address table
                //
                //
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
                    System.out.println(x);
                    Integer option = scan.nextInt();
                    if (option == 0) {
                        bre = 1;
                    } else {
                        if (!x.contains(option)) {
                            x.add(option);
                        } else {
                            System.out.println("Option already selected.");
                        }
                    }
                }
                break;
            }
            String amenQuery = "INSERT INTO Amenities (wifi, washer, ac, heating, tv, iron," +
                "kitchen,dryer,workspace, hairDryer, pool, parking,crib, grill, indoorFireplace, hotTub," +
                "evCharger, gym, breakfast, smoking, beachfront, waterfront, smokeAlarm, carbonMonoxideAlarm)" +
                " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String amenValues = "(";
            PreparedStatement query3 = con.prepareStatement(amenQuery, Statement.RETURN_GENERATED_KEYS);
            for(int i = 1; i <= 24; i++){
                boolean b = false;
                if(x.contains(i)){
                    System.out.println("Hye");
                    b = true;
                }
                query3.setBoolean(i,b);
                amenValues += b;

            }
            query = "INSERT INTO Listing (hostID,listingType,longitude,latitude,price) VALUES (?,?,?,?,?)";
            query1 = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            query1.setInt(1, user);
            query1.setString(2, listingTypes.get(opt));
            query1.setDouble(3, lon);
            query1.setDouble(4, lat);
            query1.setDouble(5, price);
            query1.executeUpdate();
            rs = query1.getGeneratedKeys();
            rs.next();
            Integer listingKey = (rs.getInt(1));

            query = "INSERT INTO Address (streetNo, streetName, city, country,province, postalCode, unitNo) VALUES (?,?,?,?,?,?)";
            PreparedStatement query2 = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            query2.setInt(1, sNo);
            query2.setString(2, sName);
            query2.setString(3,city);
            query2.setString(4,province);
            query2.setString(5,country);
            query2.setString(6, code);
            query2.setInt(7,unitNo);
            query2.executeUpdate();
            rs = query2.getGeneratedKeys();
            rs.next();
            Integer addressKey = rs.getInt(1);

            query3.executeUpdate();
            rs = query3.getGeneratedKeys();
            rs.next();
            Integer amenityKey = rs.getInt(1);

            query = "INSERT INTO Located VALUES (?,?)";
            PreparedStatement query4 = con.prepareStatement(query);
            query4.setInt(1,listingKey);
            query4.setInt(2, addressKey);
            query4.executeUpdate();

            query = "INSERT INTO Provides VALUES (?, ?)";
            PreparedStatement query5 = con.prepareStatement(query);
            query5.setInt(1,amenityKey);
            query5.setInt(2, listingKey);
            query5.executeUpdate();
            
            handleHost(user, con);
        }catch(Exception e) {
            System.out.println(e);
            createListing();
        }
    }
    public static void viewListing(){
        String query = "SELECT * FROM LISTING WHERE hostID =  ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1,user);
            ResultSet rs = statement.executeQuery();
            Integer i = 0;
            String fmt = "|%-9s|%-13s|%-10s|%-10s|%-7s|";
            System.out.println(String.format(fmt, "ListID", "Listing Type", "Longitude", "Latitude", "Price"));
            while(rs.next()){
                Integer listId = rs.getInt("listID");
                String type = rs.getString("listingType");
                Double lon = rs.getDouble("longitude");
                Double lat = rs.getDouble("latitude");
                Double price = rs.getDouble("price");
                System.out.println(String.format(fmt,listId,type,lon,lat,+price));
            }
            handleHost(user, con);
        }catch (SQLException e) {
            System.out.println(e);
        }

    }
    public static Integer validListingID(){
        System.out.println("Enter Listing ID");
        Integer listId = scan.nextInt();
        String query = "SELECT * FROM Listing WHERE listID = ?";
        try {
            PreparedStatement query1 = con.prepareStatement(query);
            query1.setInt(1, listId);
            ResultSet rs = query1.executeQuery();
            if(rs.next()) return listId;
            else return 0;
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }
    public static void deleteListing(){
        Integer listId = validListingID();
        if(listId == 0) return;
        String query;
        try {
            PreparedStatement query1;
            ResultSet rs;
            query = "SET SQL_SAFE_UPDATES = 0";
            query1 = con.prepareStatement(query);
            query1.executeUpdate();
            query =   "DELETE Listing, Located, Amenities, Address, Provides FROM Listing AS L join Located USING (listID) join Address using (addressID) join (Listing join Provides using (listID) join Amenities using (amenityID))\n" +
                "WHERE L.listID = ?";
            query1 = con.prepareStatement(query);
            query1.setInt(1,listId);

            if(5 == query1.executeUpdate()) System.out.println("Deleted Listing " + listId);
            else System.out.println("Unsuccessful");
        }catch(Exception e){
            System.out.println(e);
        }
//create table x (
//    start integer,
//            end integer
//        );
//insert into x VALUES(1,4);
//insert into x VALUES(7,10);
//insert into x VALUES(11,14);
//
//SELECT * FROM x
//WHERE x.start <= $startdate AND (end IS NULL or end >=endated)
    }
    public static void changeAvailability(){
        Integer listId = validListingID();
        if (listId == 0) return;
        System.out.println("Start Date (yyyy-mm-dd):");
        String startDate = scan.next();
        System.out.println("End Date (yyyy-mm-dd):");
        String endDate = scan.next();

        String query = "SELECT * FROM Reserved WHERE ? <= endDate AND ? >= startDate AND status = false";
        try{
            PreparedStatement query1 = con.prepareStatement(query);
            query1.setString(1,startDate);
            query1.setString(2,endDate);
            ResultSet rs = query1.executeQuery();
            if(rs.next()){
                System.out.println("Interferes with existing bookings");
                return;
            }
        }catch(Exception E){
            System.out.println(E);
        }




    }
    public static void handleHost(int input_username, Connection conn) {
        user = input_username;
        con = conn;
        //print out all the options
        System.out.println("(1) Create listing");
        System.out.println("(2) View listings");
        System.out.println("(3) View Bookings");
        System.out.println("(4) Change Availability");
        System.out.println("(5) Delete listing");
        try {

            Integer opt = scan.nextInt();
            if (opt == 1) createListing();
            else if (opt == 2) viewListing();
            else if (opt == 4) changeAvailability();
            else if (opt  == 5) deleteListing();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void createAccount(Connection con) {

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
            PreparedStatement s1 = con.prepareStatement("INSERT INTO Hosts values (?)");
            s1.setInt(1, username);
            status += s1.executeUpdate();
            if (status == 2){
                System.out.println("Successfully created.");
                Driver.mainMenu();
            } else{
                System.out.println("Not able to create account.");
                System.out.println("(0) Would you like to return to main menu?");
                if(scan.nextInt() == 0)
                    Driver.mainMenu();
                else
                    createAccount(con);
            }
        } catch (Exception e){
            System.out.println(e);
        }
        //add this user to the database
        //write prompts depending on success/fail

    }
}
