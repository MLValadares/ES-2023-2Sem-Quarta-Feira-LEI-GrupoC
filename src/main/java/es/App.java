package es;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        new App().appStart();
    }


    public void appStart() {
        int option = getFlowOption();
        switch (option) {
            case 1:
                csvToJson();
                break;
            case 2:
                jsonToCsv();
                break;
            default:
                break;
        }
        System.out.println("Exiting...");
    }

    public int getFlowOption() {
        try {
            Scanner s = new Scanner(System.in);
            System.out.print("\n1- CSV to JSON\n2- JSON to CSV\nPress any other number to exit\n\nChose the option that you want: ");
            int option = s.nextInt();
            if (option == 1 || option == 2) {
                return option;
            }
        } catch (Exception e) {
            logger.error("Error getting user's input");
        }
        return -1;
    }


    //TODO
    public void csvToJson() {
    }


    //TODO
    public void jsonToCsv() {
    }
}
