import java.sql.*;
import java.util.Hashtable;
import java.util.Scanner;

public class Host {
    public static void handleHostLogin(Connection con)  {
        Scanner scan = new Scanner(System.in);
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
                System.out.println("Incorrect Credentials");
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
        }catch(Exception e){
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
        createListing(con,username);



    }
}
