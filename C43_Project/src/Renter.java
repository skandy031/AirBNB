import java.util.Scanner;

public class Renter {
    public static void handleRenterLogin(){
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
        //send to handleRenter(user)
        scan.close();
    }

    public void handleRenter(){
        //take in user input
        int option = -1;
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Create reservation");
            System.out.println("(2) View reservations");
            System.out.println("(3) Cancel reservation");
            Scanner scan = new Scanner(System.in);
            try {
                option = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid option. Must be an integer.\n");
                scan.nextLine();
            }
        }

        if (option == 0) {
            return;
        } else if (option == 1) {
            // create reservation
        } else if (option == 2) {
            // view reservation
        } else if (option == 3){
            //delete reservation
        }else {
            System.out.println("Invalid option.\n");
            handleRenter();
        }
    }

    public void showReservations(int username){
        // display all of the reservations
    }

    public void deleteReservation(int username){
        showReservations(username);
        int option = -1;
        Scanner scan = new Scanner(System.in);
        while (true){
            try {
                System.out.println("Choose a reservation ID to cancel (-1 to exit):");
                option = scan.nextInt();
                break;
            } catch (Exception e){
                System.out.println("Invalid.\n");
            }
        }
        //search for the reservation and if exists delete it from both renter and make change in host
        if (option == -1){
            handleRenter();
            return;
        }
        scan.close();
    }

    public void createReservation(int username){
        //get dates
        //choose city
        //display all the listings that are available
        //prompt choosing an option
    }
}
