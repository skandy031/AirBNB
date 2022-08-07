import java.sql.*;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class Driver {
    static Connection con = null;
    static Scanner scan = new Scanner(System.in);

    public static void mainMenu() {

        //take in user input
        int option = -1;
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("(0) Exit System");
            System.out.println("(1) Login");
            System.out.println("(2) Create an Account");
            System.out.println("(3) Reports and Queries");
            Scanner scan = new Scanner(System.in);
            try {
                option = scan.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid option. Must be an integer.\n");
                scan.nextLine();
            }
        }
        try {
            if (option == 0) {
                return;
            } else if (option == 1) {
                User.handleLogin(con);
            } else if (option == 2) {
                User.createAccount(con);
            } else if (option == 3) {
                ReportsQueries.mainMenu(con,2);
            } else {
                System.out.println("Invalid option.\n");
                mainMenu();
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/final_project",
                    "root", "Skandium86");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/final_project",
//                    "root", "Parekh80");

            if (!con.isClosed())
                System.out.println("Successfully connected to MySQL server using TCP/IP...");

        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
//        test();
        mainMenu();
    }

}
