import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Host {
    static Scanner scan = new Scanner(System.in);
    static Connection con = null;
    static Integer user = null;

    public static void handleHostLogin(Connection conn)  {
        System.out.println("Enter username:");
        Integer username = scan.nextInt();
        System.out.println("Enter password:");
        String password = scan.next();
        con = conn;
        try{
            //Query checks Users database
            PreparedStatement query1 = con.prepareStatement("SELECT sin,password FROM Users WHERE sin = ? AND password = ?");
            query1.setInt(1, username);
            query1.setString(2, password);
            ResultSet rs1 = query1.executeQuery();

            //Query checks Hosts database
            PreparedStatement query2 = con.prepareStatement("SELECT * FROM Hosts WHERE hostID = ?");
            query2.setInt(1, username);
            ResultSet rs2 = query2.executeQuery();

            if(rs1.next() && rs2.next()){
                System.out.println("Proper");
                con = conn;
                user = username;
                handleHost();
            }
            else{
                System.out.println("Incorrect Credentials\n");
                handleHostLogin(con);

            }
        }catch(Exception e) {
            System.out.println(e);
        }
        //check to see if this matches in database
        //if it does not match
        //print out statement
        //call function again
        //if it does match
        //send to handleHost(user)
        scan.close();
    }
    public static void createListing(){
        Hashtable<Integer, String> listingTypes  = new Hashtable<Integer, String>(){{put(1,"Full house");
            put(2, "Apartment");
            put(3, "Room");}};

        try {
            System.out.println("Listing Type:");
            System.out.println("  (1) Full house");
            System.out.println("  (2) Apartment");
            System.out.println("  (3) Room");
            Integer opt = scan.nextInt();
            System.out.println("Longitude:");
            Double lon = scan.nextDouble();
            System.out.println("Latitude:");
            Double lat = scan.nextDouble();
            System.out.println("Price");
            Double price = scan.nextDouble();
            // Insert into Database
            String query = "INSERT INTO Listing (hostID,listingType,longitude,latitude,price) VALUES (?,?,?,?,?)";
            PreparedStatement query1 = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            query1.setInt(1, user);
            query1.setString(2, listingTypes.get(opt));
            query1.setDouble(3, lon);
            query1.setDouble(4, lat);
            query1.setDouble(5, price);
            query1.executeUpdate();
            ResultSet rs = query1.getGeneratedKeys();
            rs.next();
            Integer listingKey = (rs.getInt(1));
//            //Get address
            System.out.println("Street Number:");
            Integer sNo = scan.nextInt();
            System.out.println("Street Name:");
            String sName = scan.next();
            System.out.println("City:");
            String city = scan.next();
            System.out.println("Province:");
            String province = scan.next();
            System.out.println("Postal Code:");
            String code = scan.next();
            System.out.println("Unit Number:");
            Integer unitNo = scan.nextInt();

            //Insert into address table
            query = "INSERT INTO Address (streetNo, streetName, city, province, postalCode, unitNo) VALUES (?,?,?,?,?,?)";
            PreparedStatement query2 = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            query2.setInt(1, sNo);
            query2.setString(2, sName);
            query2.setString(3,city);
            query2.setString(4,province);
            query2.setString(5, code);
            query2.setInt(6,unitNo);
            query2.executeUpdate();
            rs = query2.getGeneratedKeys();
            rs.next();
            Integer addressKey = rs.getInt(1);
//
//
            System.out.println("Choose Amenities:");
            System.out.println("(1) Wifi, (2) Washer, (3) Air Conditioning, (4) Heating,\n"         +
                "(5) Television, (6) Iron, (7) Kitchen, (8) Dryer, (9) Workspace,\n" +
                "(10) Hair Dryer, (11) Pool, (12) Parking, (13) Crib, (14) Grill,\n" +
                "(15) Indoor Fireplace, (16) Hot Tub, (17) EV Charger, (18) Gym,\n"   +
                "(19) Breakfast, (20) Smoking, (21) Beachfront, (22) Waterfront\n"   +
                "(23) Smoke Alarm, (24) Carbonmononxide Alarm");
            System.out.println("Type 0 to stop");
            int bre = 0;
            List x = new ArrayList<>();
            while(bre == 0){
                System.out.println(x);
                Integer option = scan.nextInt();
                if(option == 0){
                    bre = 1;
                }
                else{
                    if(!x.contains(option)){
                        x.add(option);
                    }
                    else{
                        System.out.println("Option already selected.");
                    }
                }
            }
            System.out.println(x);
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


            query3.executeUpdate();
            rs = query3.getGeneratedKeys();
            rs.next();
            Integer amenityKey = rs.getInt(1);

            query = "INSERT INTO Located VALUES (?,?)";
            PreparedStatement query4 = con.prepareStatement(query);
            query4.setInt(1,listingKey);
            query4.setInt(2, addressKey);

            query = "INSERT INTO Provides VALUES (?, ?)";
            PreparedStatement query5 = con.prepareStatement(query);
            query5.setInt(1,amenityKey);
            query5.setInt(2, listingKey);

            handleHost();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    public static void viewListing(){
        String query = "SELECT * FROM LISTING WHERE hostID =  ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1,user);
            ResultSet rs = statement.executeQuery();
            Integer i = 0;
            while(rs.next()){
//                String
                i++;
            }
            handleHost();
        }catch (SQLException e) {
            System.out.println(e);
        }

    }
    public static void handleHost() {
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
