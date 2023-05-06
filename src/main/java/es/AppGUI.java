package es;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.json.CDL;

import javax.servlet.DispatcherType;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class AppGUI extends JFrame {

    public static final Logger logger = LogManager.getLogger(AppGUI.class);

    private JTextField inputFileTextField;
    private JRadioButton csvToJsonRadioButton;
    private JRadioButton jsonToCsvRadioButton;
    private String error = "Error";

    private transient Server server;


    public static void main(String[] args) {
        AppGUI app = new AppGUI();
        app.start();
    }


    public boolean start() {
        initComponents();
        return true;
    }

    /**
     * Description: Esta função serve para construir o GUI que irá ser utilizado pelo utilizador para converter os ficheiros
     */
    public void initComponents() {
        JButton launchHtmlButton;
        JButton seeOverlapButton;
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

        launchHtmlButton.addActionListener(e -> launchHtml(inputFileTextField.getText(), false));
        buttonPanel.add(launchHtmlButton);

        // Set up the launch HTML button
        seeOverlapButton = new JButton("See Overlaps/Overcrows");
        seeOverlapButton.addActionListener(e -> launchHtml(inputFileTextField.getText(), true));
        buttonPanel.add(seeOverlapButton);

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



    public boolean csvToJson(String inputFileOrUrl) {
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
        return true;
    }

    public boolean jsonToCsv(String inputFileOrUrl) {
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
        return true;
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

    public boolean launchHtml(String inputFileOrUrl, boolean overlap) {
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
                launchHtmlWithJson(jsonFileName, overlap);
            } catch (IOException e) {
                logger.error("Error converting CSV to JSON", e);
                JOptionPane.showMessageDialog(this, "Error converting CSV to JSON: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e) {
                logger.error("Invalid CSV format", e);
                JOptionPane.showMessageDialog(this, "Invalid CSV format: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
            }
        } else if (fileExtension.equals("json")) {
            // directly launch HTML page with JSON file
            launchHtmlWithJson(inputFileOrUrl, overlap);
        } else {
            // invalid file type
            JOptionPane.showMessageDialog(this, "Invalid file type: " + fileExtension, error, JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private void launchHtmlWithJson(String jsonFilePath, boolean seeOverlap) {
        try {
            String relativePath;
            if(seeOverlap)
                relativePath = "src/main/resources/overlap_calendar.html";
            else
                relativePath = "src/main/resources/calendar.html";
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


    private void launchServer(String file) {

        server = new Server(8080);

        ServletContextHandler icsHandler = new ServletContextHandler();
        icsHandler.setContextPath("/");
        icsHandler.setResourceBase(".");
        icsHandler.addServlet(CalendarServlet.class, "/fenix_calendar.ics");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[] { "index.html" });
        resourceHandler.setResourceBase("src/main/resources/" + file);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, icsHandler, new DefaultHandler() });

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.setHandler(handlers);
        servletContextHandler.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));


        server.setHandler(servletContextHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }
    }

    private void stopServer() {
        if(server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public boolean getCalendarButtonActionPerformed() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Fenix calendar URL:");
        JTextField urlTextField = new JTextField(30);
        inputPanel.add(urlLabel);
        inputPanel.add(urlTextField);

        JButton webButton = new JButton("See in Web");
        JButton calendarButton = new JButton("Make new calendar");
        JButton cancelButton = new JButton("Cancel");
        inputPanel.add(webButton);
        inputPanel.add(calendarButton);
        inputPanel.add(cancelButton);

        webButton.addActionListener(e -> {
            try {
                String url = urlTextField.getText();
                url = url.replace("webcal://", "https://");
                saveIcsFile(url);

                launchHtmlWithIcs(false, "http://localhost:8080/");

                closeInputPanel(inputPanel);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error downloading calendar: " + ex.getMessage());
            }
        });

        calendarButton.addActionListener(e -> {
            try {
                String url = urlTextField.getText();
                url = url.replace("webcal://", "https://");
                saveIcsFile(url);

                launchHtmlWithIcs(true, "http://localhost:8080/");

                closeInputPanel(inputPanel);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error downloading calendar: " + ex.getMessage());
            }
        });


        cancelButton.addActionListener(e -> closeInputPanel(inputPanel));


        JDialog dialog = new JDialog();
        dialog.setContentPane(inputPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
        dialog.setVisible(true);
        return true;
    }


    private void saveIcsFile(String url) throws IOException{
        URL website = new URL(url);
        InputStream in = website.openStream();
        Path path = Paths.get("src/main/resources/fenix_calendar.ics");
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
    }


    private void closeInputPanel(JPanel inputPanel) {
        Window window = SwingUtilities.getWindowAncestor(inputPanel);
        if (window != null) {
            window.dispose();
        }
    }


    private void launchHtmlWithIcs(boolean newCalendar, String url) {
        try {
            Thread serverThread = new Thread(() -> {
                stopServer();
                if (newCalendar)
                    launchServer("new_calendar_from_another.html");
                else {
                    launchServer("fenix_calendar.html");
                }

            });
            serverThread.start();
            URI uri = new URI(url);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException e) {
            logger.error("Error launching HTML page", e);
            JOptionPane.showMessageDialog(this, "Error launching HTML page: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        } catch (URISyntaxException e) {
            logger.error("Error launching server", e);
            JOptionPane.showMessageDialog(this, "Error launching server: " + e.getMessage(), error, JOptionPane.ERROR_MESSAGE);
        }
    }

}
