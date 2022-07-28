import java.sql.*;
import java.util.Hashtable;
import java.util.Scanner;

public class Host {
    static Scanner scan = new Scanner(System.in);
    public static void makeQueries(Connection con, String cols, String where, String table){
      String query = String.format("SELECT %s FROM %s",cols,table);
      if (!where.equals("")){
          query = String.format("%s WHERE %s",query, where);
      }
      try{
          PreparedStatement q = con.prepareStatement(query);

      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
    }
    public static void handleHostLogin(Connection con)  {
        System.out.println("Enter username:");
        Integer username = scan.nextInt();
        System.out.println("Enter password:");
        String password = scan.next();
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
                handleHost(con, username);
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
    public static void listingType(){}
    public static void createListing(Connection con, Integer username){
        Hashtable<Integer, String> listingTypes  = new Hashtable<Integer, String>(){{put(1,"Full house");
            put(2, "Apartment");
            put(3, "Room");}};

        try {
            System.out.println("Listing Type:");
            System.out.println("  (1) Full house");
            System.out.println("  (2) Apartment");
            System.out.println("  (3) Room");
            Scanner scan = new Scanner(System.in);
            Integer opt = scan.nextInt();
            System.out.println("Longitude:");
            Double lon = scan.nextDouble();
            System.out.println("Latitude:");
            Double lat = scan.nextDouble();
            System.out.println("Price");
            Double price = scan.nextDouble();

            // Insert into Database
            String query = "INSERT INTO Listing (hostID,listingType,longitude,latitude,price) VALUES (?,?,?,?,?)";
            PreparedStatement query1 = con.prepareStatement(query);
            query1.setInt(1, username);
            query1.setString(2, listingTypes.get(opt));
            query1.setDouble(3, lon);
            query1.setDouble(4, lat);
            query1.setDouble(5, price);
            query1.executeUpdate();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    public static void handleHost(Connection con, Integer username) {
        //print out all the options
        System.out.println("(1) Create listing");
        System.out.println("(2) View listings");
        System.out.println("(3) View Bookings");
        System.out.println("(4) Change Availability");
        System.out.println("(5) Delete listing");
        try {
            Integer opt = scan.nextInt();
            if(opt == 1) createListing(con,username);
            else if(opt == 2);
        }catch(Exception e){
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
            if (status == 1){
                System.out.println("Successfully created.");
            } else{
                System.out.println("Not able to create account.");
            }
        } catch (Exception e){
            System.out.println(e);
        }
        //add this user to the database
        //write prompts depending on success/fail

    }
}
