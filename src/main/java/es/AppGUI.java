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


/**

 Classe para interface gráfica de usuário que permite conversão de arquivos CSV para JSON e vice-versa,

 bem como visualização de calendário em formato HTML. Permite ainda buscar um calendário da plataforma

 Fenix e salvar o resultado em um arquivo.

 */

public class AppGUI extends JFrame {

    /**

     Classe que representa um objeto Logger, usado para registrar mensagens de log em uma aplicação.
     O Logger é criado a partir da classe AppGUI usando a biblioteca LogManager.
     As mensagens de log registradas pelo Logger podem ajudar a identificar problemas e depurar o código da aplicação.

     */

    public static final Logger logger = LogManager.getLogger(AppGUI.class);


    /**
     * Representa um campo de texto para entrada de caminho de arquivo.
     *

     */

    private JTextField inputFileTextField;

    /**

     Representa um botão de rádio para conversão de CSV para JSON.
     */

    private JRadioButton csvToJsonRadioButton;

    /**

     Representa um botão de opção para selecionar a conversão de JSON para CSV.
     */

    private JRadioButton jsonToCsvRadioButton;

    /**

     Representa uma string de erro.
     */

    private String error = "Error";


    /**

     Representa um objeto do tipo Server que será utilizado para executar um servidor.
     O objeto é marcado como "transient" para indicar que ele não deve ser serializado
     em conjunto com o objeto que o contém.
     */

    private transient Server server;

    /**

     Método principal que inicia a aplicação Fenix.
     Cria uma nova instância da classe AppGUI e chama o método start().
     @param args Argumentos passados por linha de comando (não são utilizados)
     */

    public static void main(String[] args) {
        AppGUI app = new AppGUI();
        app.start();
    }

    /**

     Inicializa os componentes gráficos da aplicação.
     @return true se a inicialização foi bem sucedida.
     */

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

    /**
     * Converte um arquivo CSV em um arquivo JSON ou um arquivo JSON em um arquivo CSV.
     *
     * Este método é chamado quando o usuário clica no botão "Converter" da interface gráfica. Ele determina qual opção de conversão (CSV para JSON ou JSON para CSV)
     * foi selecionada pelo usuário e chama o método apropriado para converter o arquivo. Em seguida, encerra o aplicativo. Se ocorrer uma exceção, uma mensagem de erro será registrada no log.
     *
     * @throws IllegalArgumentException se a opção selecionada pelo usuário for inválida
     */

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

    /**
     * Converte um arquivo CSV em um arquivo JSON.
     *
     * @param inputFileOrUrl o caminho do arquivo CSV de entrada ou URL a ser convertido em JSON
     * @return true se a conversão foi bem sucedida, false caso contrário

     * @throws JSONException se o arquivo CSV tiver um formato inválido
     */

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

    /**
     * Converte um arquivo JSON em um arquivo CSV.
     *
     * @param inputFileOrUrl o caminho do arquivo JSON de entrada ou URL a ser convertido em CSV
     * @return true se a conversão foi bem sucedida, false caso contrário

     * @throws JSONException se o arquivo JSON tiver um formato inválido
     */

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

    /**
     * Retorna um objeto InputStream para um arquivo local ou URL.
     *
     * @param inputFileOrUrl o caminho do arquivo local ou URL para obter o InputStream
     * @return um objeto InputStream para o arquivo especificado
     * @throws IOException se ocorrer um erro de entrada/saída ao abrir o arquivo ou URL
     */

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

    /**

     Salva o conteúdo de um arquivo em disco. Abre um JFileChooser para que o usuário escolha o local de salvamento.
     @param type O tipo de arquivo a ser salvo (exemplo: "JSON", "CSV").
     @param content O conteúdo do arquivo a ser salvo.
     @throws java.io.IOException se ocorrer um erro de I/O durante a operação de escrita do arquivo.
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

    /**

     Lança uma página HTML a partir de um arquivo de entrada ou URL, podendo opcionalmente sobrepor janelas.
     Se o arquivo de entrada é um arquivo CSV, ele será convertido para JSON antes de ser aberto na página HTML.
     @param inputFileOrUrl o caminho do arquivo de entrada ou URL
     @param overlap um booleano que indica se a nova janela deve ser sobreposta a outras janelas
     @return true se a página HTML foi aberta com sucesso, caso contrário, false

     @throws JSONException se o formato do arquivo CSV for inválido
     */

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

    /**

     Lança uma página HTML contendo um calendário a partir de um arquivo JSON. O arquivo JSON pode ser fornecido como um caminho absoluto ou um URL.
     @param jsonFilePath o caminho absoluto ou URL do arquivo JSON a ser usado como entrada.
     @param seeOverlap se true, a página HTML exibirá sobreposições de eventos no calendário, caso contrário, não exibirá.

     */

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

    /**

     Inicia um servidor na porta 8080 e configura os manipuladores de contexto para servir um arquivo e um servlet.
     @param file O nome do arquivo que será servido.

     */

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

    /**

     Para o servidor em execução, se houver algum.

     */

    private void stopServer() {
        if(server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**

     Exibe um painel com uma entrada de texto para a URL do calendário Fenix, além de botões para visualizar o calendário na web ou criar um novo arquivo .ics.
     @return true, se o botão do calendário foi clicado com sucesso

     */

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

    /**

     Salva o arquivo ICS a partir de uma URL fornecida.
     @param url URL do arquivo ICS a ser baixado e salvo.
     @throws IOException se ocorrer um erro de E/S durante a operação de download e salvamento do arquivo.
     */

    private void saveIcsFile(String url) throws IOException{
        URL website = new URL(url);
        InputStream in = website.openStream();
        Path path = Paths.get("src/main/resources/fenix_calendar.ics");
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
    }


    /**

     Encerra o painel de entrada fornecido como argumento.
     @param inputPanel o painel de entrada que será fechado
     */

    private void closeInputPanel(JPanel inputPanel) {
        Window window = SwingUtilities.getWindowAncestor(inputPanel);
        if (window != null) {
            window.dispose();
        }
    }

    /**

     Lança uma página HTML e inicia um servidor para servir a página junto com o arquivo ICS correspondente.
     @param newCalendar se for true, lançará a página "new_calendar_from_another.html", caso contrário, lançará "fenix_calendar.html".
     @param url a URL da página que será aberta no navegador padrão.

     */

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
