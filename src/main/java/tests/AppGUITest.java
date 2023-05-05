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
		assertTrue(app.csvToJson("C:\\Users\\Miguel\\git\\ES-2023-2Sem-Quarta-Feira-LEI-GrupoC\\src\\main\\resources\\horario-exemplo.csv"));
	}
	
	@Test
	void convertJSONtoCSVLocalTest(){
		assertTrue(app.jsonToCsv("C:\\Users\\Miguel\\git\\ES-2023-2Sem-Quarta-Feira-LEI-GrupoC\\src\\main\\resources\\output.json"));
	}
	
	@Test
	void launchHTML() {
		assertTrue(app.launchHtml("C:\\Users\\Miguel\\git\\ES-2023-2Sem-Quarta-Feira-LEI-GrupoC\\src\\main\\resources\\horario-exemplo.csv"));
	}

//	@Test
//	void test() {
//		assertTrue(app.start());
//	}
	
//	@Test
//	void ah() {
//		app.launchHtmlWithIcs(true);
//	}
}
