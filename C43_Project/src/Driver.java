import java.util.Scanner;

public class Driver {

    public static void handleHostLogin(){
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

    public void handleHost(String username){
        //print out all the options
        System.out.println("(1) Create listing");
        System.out.println("(2) View listings");
        System.out.println("(3) Delete listing");



    }

    public void handleRenter(){
        System.out.println("(1) Create reservation");
        System.out.println("(2) View reservations");
        System.out.println("(3) Delete listing");
    }





    public static void createAccount(){
        Scanner scan = new Scanner(System.in);
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
        scan.close();

        //add this user to the database
        //write prompts depending on success/fail
    }

    public static void mainMenu(){

        //take in user input
        int option = -1;
        while (true){
            System.out.println("Choose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Login as Host");
            System.out.println("(2) Login as Renter");
            System.out.println("(3) Create an account");
            Scanner scan = new Scanner(System.in);
            try{
                option = scan.nextInt();
                break;
            } catch (Exception e){
                System.out.println("Invalid option. Must be an integer.\n");
                scan.nextLine();
            }
        }

        if (option == 0){
            return;
        }
        else if (option == 1){
            handleHostLogin();
        } else if (option == 2) {
            handleRenterLogin();
        } else if (option == 3) {
            createAccount();
            mainMenu();
        }
        else{
            System.out.println("Invalid option.\n");
            mainMenu();
        }

    }

    public static void main(String [] args) {
        mainMenu();
    }
}
