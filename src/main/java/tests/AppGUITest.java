package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.AppGUI;

class AppGUITest {
	private AppGUI app;
	private String k;

	@BeforeEach
	void setUp() throws Exception {
		k = System.getProperty("user.dir");
		this.app = new AppGUI();
	}
	
	@Test
	void convertCSVtoJSONURLTest(){
		assertTrue(app.csvToJson("https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv"));
	}
	
	@Test
	void convertCSVtoJSONLocalTest(){
		assertTrue(app.csvToJson(k + "\\src\\main\\resources\\horario-exemplo.csv"));
	}
	
	@Test
	void convertJSONtoCSVLocalTest(){
		assertTrue(app.jsonToCsv(k + "\\src\\main\\resources\\output.json"));
	}
	
	@Test
	void launchHTMLCSV() {
		assertTrue(app.launchHtml(k + "\\src\\main\\resources\\horario-exemplo.csv", false));
	}
	
	@Test
	void launchHTMLJSON() {
		assertTrue(app.launchHtml(k + "\\src\\main\\resources\\output.csv"));
	}
	
	@Test
	void getCalendarButtonActionPerformedTest() {
		app.getCalendarButtonActionPerformed();
	}

}
