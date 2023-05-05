package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.AppGUI;

class AppGUITest {
	private AppGUI app;

	@BeforeEach
	void setUp() throws Exception {
		this.app = new AppGUI();
	}
	
	@Test
	void convertCSVtoJSONURLTest(){
		assertTrue(app.csvToJson("https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv"));
	}
	
	@Test
	void convertCSVtoJSONLocalTest(){
		String k = System.getProperty("user.dir");
		assertTrue(app.csvToJson(k + "\\src\\main\\resources\\horario-exemplo.csv"));
	}
	
	@Test
	void convertJSONtoCSVLocalTest(){
		String k = System.getProperty("user.dir");
		assertTrue(app.jsonToCsv(k + "\\src\\main\\resources\\output.json"));
	}
	
	@Test
	void launchHTMLCSV() {
		String k = System.getProperty("user.dir");
		assertTrue(app.launchHtml(k + "\\src\\main\\resources\\horario-exemplo.csv"));
	}
	
	@Test
	void launchHTMLJSON() {
		String k = System.getProperty("user.dir");
		assertTrue(app.launchHtml(k + "\\src\\main\\resources\\output.csv"));
	}
	
	@Test
	void getCalendarButtonActionPerformedTest() {
		app.getCalendarButtonActionPerformed();
	}

}
