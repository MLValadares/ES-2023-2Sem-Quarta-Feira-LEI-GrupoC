package es;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**

 Servlet responsável por gerar o arquivo .ics para download do calendário do Fenix.
 */

public class CalendarServlet extends HttpServlet {

    /**

     Método que lida com uma requisição GET HTTP para retornar um arquivo de calendário iCalendar.
     @param request o objeto HttpServletRequest que contém a requisição do cliente
     @param response o objeto HttpServletResponse que contém a resposta a ser enviada ao cliente
     @throws ServletException se houver um erro de servlet
     @throws IOException se houver um erro de entrada ou saída
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/calendar");
        response.setHeader("Content-Disposition", "attachment; filename=fenix_calendar.ics");

        // Read the file contents and write them to the response
        try (InputStream in = getClass().getResourceAsStream("/fenix_calendar.ics");
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}