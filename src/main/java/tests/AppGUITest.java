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
	void test() {
		assertTrue(app.initComponents());
	}
//	
//	@Test
//	void saveFileTest() {
//		String inputFileOrUrl = "C:\\Users\\Miguel\\Desktop\\ES\\res\\horario-exemplo.csv";
//		assertTrue(app.csvToJson(inputFileOrUrl));
//	}

}
