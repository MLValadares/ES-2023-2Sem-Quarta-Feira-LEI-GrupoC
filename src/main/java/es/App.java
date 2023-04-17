package es;

import org.json.CDL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONTokener;


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
        InputStream inputStream = getInputStream();
        String csvAsString = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        try {
            String json = CDL.toJSONArray(csvAsString).toString();
            saveFile(json.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            logger.fatal("A fatal error occurred, please check if your .csv file is correctly formatted.");
        }
    }


    public void jsonToCsv() {
        InputStream inputStream = getInputStream();
        try {
            JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));
            saveFile(CDL.toString(jsonArray).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            logger.fatal("A fatal error occurred, please check if your .json file is correctly formatted.");
        }
    }
    //TODO
    public void saveFile(byte[] bytes) {

    }


    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the file path or remote URL: ");
            String fileOrUrl = reader.readLine();

            if (fileOrUrl.startsWith("http") || fileOrUrl.startsWith("https")) {
                URL url = new URL(fileOrUrl);
                URLConnection connection = url.openConnection();
                inputStream = connection.getInputStream();
            } else {
                inputStream = new FileInputStream(fileOrUrl);
            }
        } catch (MalformedURLException e) {
            logger.error("Invalid URL");
        } catch (FileNotFoundException e) {
            logger.error("File not found");
        } catch (IOException e) {
            logger.error("Error reading file");
        }
        return inputStream;
    }
}
