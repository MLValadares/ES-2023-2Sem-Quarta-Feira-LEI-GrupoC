package es;

import org.json.CDL;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class AppGUI extends JFrame {

    private static final Logger logger = LogManager.getLogger(AppGUI.class);

    private JTextField inputFileTextField;
    private JRadioButton csvToJsonRadioButton;
    private JRadioButton jsonToCsvRadioButton;
    private String error = "Error";

    public static void main(String[] args) {
        AppGUI app = new AppGUI();
        app.start();
    }

    public void start() {
        initComponents();
    }

    private void initComponents() {
        JButton launchHtmlButton;
        JButton convertButton;
        JLabel inputLabel;
        // Set up the main frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calendar Tools");
        setResizable(false);

        // Set up the content pane
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridLayout(0, 1));
        setContentPane(contentPane);

        // Set up the input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        inputLabel = new JLabel("Input File/URL:");
        inputFileTextField = new JTextField(30);
        inputPanel.add(inputLabel);
        inputPanel.add(inputFileTextField);
        contentPane.add(inputPanel);

        // Set up the radio button panel
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup buttonGroup = new ButtonGroup();
        csvToJsonRadioButton = new JRadioButton("CSV to JSON");
        jsonToCsvRadioButton = new JRadioButton("JSON to CSV");
        buttonGroup.add(csvToJsonRadioButton);
        buttonGroup.add(jsonToCsvRadioButton);
        csvToJsonRadioButton.setSelected(true);
        radioButtonPanel.add(csvToJsonRadioButton);
        radioButtonPanel.add(jsonToCsvRadioButton);
        contentPane.add(radioButtonPanel);

        // Set up the convert button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        convertButton = new JButton("Convert");
        convertButton.addActionListener(e -> convertButtonActionPerformed());
        buttonPanel.add(convertButton);

        // Set up the launch HTML button
        launchHtmlButton = new JButton("Launch HTML");
        launchHtmlButton.addActionListener(e -> launchHtml(inputFileTextField.getText()));
        buttonPanel.add(launchHtmlButton);

        contentPane.add(buttonPanel);

        JButton getCalendarButton = new JButton("Get calendar from Fenix");
        getCalendarButton.addActionListener(e -> getCalendarButtonActionPerformed());
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel2.add(convertButton);
        buttonPanel2.add(launchHtmlButton);
        buttonPanel2.add(getCalendarButton);
        contentPane.add(buttonPanel2);


        // Pack the frame and center it on the screen
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void convertButtonActionPerformed() {
        int option = 0;
        if (csvToJsonRadioButton.isSelected()) {
            option = 1;
        } else if (jsonToCsvRadioButton.isSelected()) {
            option = 2;
        }
        String inputFileOrUrl = inputFileTextField.getText();
        try {
            switch (option) {
                case 1:
                    csvToJson(inputFileOrUrl);
                    break;
                case 2:
                    jsonToCsv(inputFileOrUrl);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid option: " + option);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid option");
        }
        System.exit(0);
    }



    public void csvToJson(String inputFileOrUrl) {
        try (InputStream inputStream = getInputStream(inputFileOrUrl);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String csvAsString = reader.lines().collect(Collectors.joining("\n"));
            String json = CDL.toJSONArray(csvAsString).toString();
            saveFile("JSON", json);
        } catch (IOException e) {
            logger.error("Error converting CSV to JSON", e);
            JOptionPane.showMessageDialog(this, "Error converting CSV to JSON: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        } catch (JSONException e) {
            logger.error("Invalid CSV format", e);
            JOptionPane.showMessageDialog(this, "Invalid CSV format: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void jsonToCsv(String inputFileOrUrl) {
        try (InputStream inputStream = getInputStream(inputFileOrUrl);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            JSONArray jsonArray = new JSONArray(new JSONTokener(reader));
            String csv = CDL.toString(jsonArray);
            saveFile("CSV", csv);
        } catch (IOException e) {
            logger.error("Error converting JSON to CSV", e);
            JOptionPane.showMessageDialog(this, "Error converting JSON to CSV: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        } catch (JSONException e) {
            logger.error("Invalid JSON format", e);
            JOptionPane.showMessageDialog(this, "Invalid JSON format: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        }
    }

    public InputStream getInputStream(String inputFileOrUrl) throws IOException {
        InputStream inputStream;
        if (inputFileOrUrl.startsWith("http") || inputFileOrUrl.startsWith("https")) {
            URL url = new URL(inputFileOrUrl);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
        } else {
            inputStream = new FileInputStream(inputFileOrUrl);
        }
        return inputStream;
    }

    private void saveFile(String type, String content) throws java.io.IOException{
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save " + type + " File");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                fileWriter.write(content);
                fileWriter.flush();
            }
            JOptionPane.showMessageDialog(this, "Successfully converted to: " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void launchHtml(String inputFileOrUrl) {
        String fileExtension = inputFileOrUrl.substring(inputFileOrUrl.lastIndexOf(".") + 1).toLowerCase();
        if (fileExtension.equals("csv")) {
            // convert CSV to JSON first
            try (InputStream inputStream = getInputStream(inputFileOrUrl);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String csvAsString = reader.lines().collect(Collectors.joining("\n"));
                String json = CDL.toJSONArray(csvAsString).toString();
                String jsonFileName = inputFileOrUrl.substring(0, inputFileOrUrl.lastIndexOf(".")) + ".json";
                try (FileWriter fileWriter = new FileWriter(jsonFileName)) {
                    fileWriter.write(json);
                }
                launchHtmlWithJson(jsonFileName);
            } catch (IOException e) {
                logger.error("Error converting CSV to JSON", e);
                JOptionPane.showMessageDialog(this, "Error converting CSV to JSON: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e) {
                logger.error("Invalid CSV format", e);
                JOptionPane.showMessageDialog(this, "Invalid CSV format: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
            }
        } else if (fileExtension.equals("json")) {
            // directly launch HTML page with JSON file
            launchHtmlWithJson(inputFileOrUrl);
        } else {
            // invalid file type
            JOptionPane.showMessageDialog(this, "Invalid file type: " + fileExtension, error, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchHtmlWithJson(String jsonFilePath) {
        try {
            String relativePath = "src/main/resources/calendar.html";
            File file = new File(relativePath);
            URI uri = file.toURI();
            String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            String jsString = "var myData = " + jsonString + ";";
            String jsFilePath = "src/main/resources/data.js";
            try (FileWriter fileWriter = new FileWriter(jsFilePath)) {
                fileWriter.write(jsString);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException e) {
            logger.error("Error launching HTML page", e);
            JOptionPane.showMessageDialog(this, "Error launching HTML page: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getCalendarButtonActionPerformed() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Fenix calendar URL:");
        JTextField urlTextField = new JTextField(30);
        inputPanel.add(urlLabel);
        inputPanel.add(urlTextField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Enter Fenix Calendar URL", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String url = urlTextField.getText();
            url = url.replace("webcal://", "https://");

            try {
                URL website = new URL(url);
                InputStream in = website.openStream();
                Files.copy(in, Paths.get("src/main/resources/fenix_calendar.ics"), StandardCopyOption.REPLACE_EXISTING);
                launchHtmlWithIcs();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error downloading calendar: " + e.getMessage());
            }
        }
    }

    private void launchHtmlWithIcs() {
        try {
            String relativePath = "src/main/resources/fenix_calendar.html";
            File file = new File(relativePath);
            URI uri = file.toURI();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException e) {
            logger.error("Error launching HTML page", e);
            JOptionPane.showMessageDialog(this, "Error launching HTML page: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        }
    }




}