import java.sql.*;
import java.util.Scanner;

public class Host {
      public static void handleHostLogin(Connection con) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scan.next();
        System.out.println("Enter password:");
        String password = scan.next();
        //check to see if this matches in database
        //if it does not match
        //print out statement
        //call function again
        //if it does match
        //send to handleHost(user)
        scan.close();
    }


    public void handleHost(String username) {
        //print out all the options
        System.out.println("(1) Create listing");
        System.out.println("(2) View listings");
        System.out.println("(3) Delete listing");


    }
}
