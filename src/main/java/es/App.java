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



/**

 Classe responsável por ler um arquivo .csv e transformá-lo em um arquivo .json, ou ler um arquivo .json e transformá-lo em um arquivo .csv.

 Possui métodos que realizam essas conversões, além de um método para obter o fluxo de conversão escolhido pelo usuário.
 */

public class App {

    /**

     O logger é uma instância da classe LogManager para fazer logging de mensagens de erro, informações e outras ações importantes do aplicativo.
     É uma variável estática final, o que significa que o objeto criado será o mesmo para todas as instâncias da classe App e não pode ser alterado durante a execução do programa.

     */

    private static final Logger logger = LogManager.getLogger(App.class);


    /**

     Método principal que inicia a aplicação.
     @param args os argumentos de linha de comando (não são utilizados)

     */

    public static void main(String[] args) {
        new App().appStart();
    }


    /**

     Inicia a aplicação e executa a opção escolhida pelo usuário.
     @return verdadeiro se a execução foi bem sucedida, falso caso contrário.
     */

    public boolean appStart() {
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
        return true;
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
     * Converte um arquivo CSV em um arquivo JSON e salva-o.
     *
     * Este método lê um arquivo CSV do fluxo de entrada fornecido e o converte em uma string CSV. Em seguida, converte a string CSV em uma matriz JSON
     * usando a biblioteca CDL e salva o resultado em um arquivo. Se ocorrer uma exceção, uma mensagem de erro será registrada no log.
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
     * Converte um arquivo JSON em um arquivo CSV e salva-o.
     *
     * Este método lê um arquivo JSON do fluxo de entrada fornecido e o converte em uma matriz JSON. Em seguida, converte a matriz JSON
     * em uma string CSV usando a biblioteca CDL e salva o resultado em um arquivo. Se ocorrer uma exceção, uma mensagem de erro será registrada no log.
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
     * Salva um array de bytes em um arquivo.
     *
     * Este método grava um array de bytes em um arquivo especificado pelo usuário. Ele solicita ao usuário que forneça o caminho para o arquivo
     * de saída e, em seguida, grava os bytes no arquivo usando a classe Files da API Java NIO. Se ocorrer uma exceção, uma mensagem de erro será exibida no console.
     *
     * @param bytes o array de bytes a ser salvo no arquivo

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

     Obtém um InputStream a partir de um arquivo local ou URL remoto informado pelo usuário através da entrada padrão.
     @return o InputStream contendo os dados do arquivo ou URL informado.


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