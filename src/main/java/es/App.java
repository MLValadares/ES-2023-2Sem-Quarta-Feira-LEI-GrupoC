package es;

import org.json.CDL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
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


    /**
     * Description: Esta função irá utilizar o valor option obtido através da função GetFlowOption() para determinar se vamos converter um ficheiro CSV para Json ou vice-versa
     *              Ao terminar a conversão escreve "Exiting..." na consola
     *
     */
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

    /**
     * Description: Esta função pergunta ao user se pretende converter um ficheiro do tipo CSV para Json ou um ficheiro do tipo Json para CSV
     *
     * @return -1 se a função falhar / option sendo option a opção escolhida pelo utilizador que apenas pode assumir os valores 1 ou 2
     *
     */
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


    /**
     * Description: Esta função guarda na variável csvAsString o nome do ficheiro CSV que irá ser transformado numa string Json e salvo como ficheiro Json
     *              Caso haja um erro na transição da string CSV para a string Json um erro é mostrado na consola
     *
     *
     */
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


    /**
     * Description:Esta função guarda na variável jsonArray o input do utilizador que irá ser salvo como ficheiro CSV
     *             Caso haja um erro na transição da string json para a string CSV um erro é mostrado na consola
     *
     *
     *
     */
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

    /**
     * Description: Esta função irá salvar o ficheiro dado como input na consola com o conteúdo dado nos parãmetros de entrada
     *
     * @param bytes sendo bytes o conteudo do arquivo que irá ser salvo
     */
    public void saveFile(byte[] bytes) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the path where you want to save the output: ");
            String outputPath = reader.readLine();
            System.out.println("\nConverting...");
            Files.write(Path.of(outputPath), bytes);
            System.out.println("\nSuccessfully converted to: src/main/resources/output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Description: Esta função através do input utilizador guarda o path do ficheiro ou o URl do ficheiro na variável fileorUrl e caso a variável comece por http ou https,
     *            indicando que é um URL, faz a conexão  guardando o ficheiro na variável inputStream, caso seja um path, guarda o path na variável inputStream
     *
     * @return inputStream sendo inputStream o input dado pelo utilizador, podendo este ser um URL ou um PATH do ficheiro
     */
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