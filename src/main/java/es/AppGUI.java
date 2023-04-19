
package es;

import org.json.CDL;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
    private String error = "Error";

    public static void main(String[] args) {
        AppGUI app = new AppGUI();
        app.start();
    }

    /**
     * Description: Esta função irá iniciar a GUI
     */
    public void start() {
        initComponents();
    }

    /**
     * Description: Esta função serve para construir o GUI que irá ser utilizado pelo utilizador para converter os ficheiros
     */
    public boolean initComponents() {
        JRadioButton jsonToCsvRadioButton;
        JButton convertButton;
        JLabel inputLabel;
        // Set up the main frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("CSV/JSON Converter");
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
        contentPane.add(buttonPanel);

        // Pack the frame and center it on the screen
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
		return true;
    }

    /**
     * Description: Esta função irá guardar na variável option os valores 1 ou 2 dependendo que opção o utilizador escolher selecionar no radioButton
     *            para converter ou de um ficheiro CSV para Json ou Json para CSV respetivamente. Ao acabar a conversão o programa acaba
     */
    private void convertButtonActionPerformed() {
        int option = csvToJsonRadioButton.isSelected() ? 1 : 2;
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


    /**
     * Description: Esta função através do input do utilizador irá fazer a conversão do tipo do ficheiro e caso haja um erro no input do user
     *            é mostrada a mensagem de erro relativa à IOException ou o erro esteja na transição do formato é mostrada a mensagem de erro
     *            relativa à JSONException
     *
     * @param inputFileOrUrl sendo inputFileOrUrl o path ou URL do ficheiro introduzido pelo user
     */
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

    /**
     * Description: Esta função através do input do utilizador irá fazer a conversão do tipo do ficheiro e caso haja um erro no input do user
     *            é mostrada a mensagem de erro relativa à IOException ou o erro esteja na transição do formato é mostrada a mensagem de erro
     *            relativa à JSONException
     *
     * @param inputFileOrUrl sendo inputFileOrUrl o path ou URL do ficheiro introduzido pelo user
     */
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

    /**
     * Description: Esta função, através do inputFileorUrl, sendo este a string do path ou do URL do ficheiro, guarda o input do utilizador na variável
     *            inputStream para esta ser utilizada na conversão
     *
     * @param inputFileOrUrl sendo inputFileorURl o path ou o URL do ficheiro introduzido pelo user
     * @return inputStream sendo inputStream o URL ou o path do ficheiro introduzido pelo user em formato InputStream
     * @throws IOException caso haja um erro relativamente ao input do utilizador
     */
    public InputStream getInputStream(String inputFileOrUrl) throws IOException {
        InputStream inputStream = null;
        if (inputFileOrUrl.startsWith("http") || inputFileOrUrl.startsWith("https")) {
            URL url = new URL(inputFileOrUrl);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
        } else {
            inputStream = new FileInputStream(inputFileOrUrl);
        }
        return inputStream;
    }

    /**
     * Description: Esta função serve para guardar o conteúdo de um ficheiro, dado pelo parâmetro content, no tipo pretendido, dado pelo parâmetro type
     *
     * @param type sendo type o tipo que o ficheiro irá ter
     * @param content sendo content o conteúdo do ficheiro que irá ser guardado
     * @throws java.io.IOException caso haja um erro ao guardar o ficheiro
     */
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
}

