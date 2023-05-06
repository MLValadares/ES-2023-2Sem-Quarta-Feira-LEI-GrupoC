package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.App;

class AppTest {
	private App app;
	private String k;

	@BeforeEach
	void setUp() throws Exception {
		k = System.getProperty("user.dir");
		this.app = new App();
	}

	@Test
	void test() {
		System.out.println("Teste base");
		System.out.println("Introduzir 3");
		assertTrue(app.appStart());
	}
	
	@Test
	void convertCSVtoJSONURLTest() {
		System.out.println("Teste de converter ficheiro CSV via URL");
		System.out.println("Introduzir 1");
		System.out.println("Deve Introduzir: https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv");
		assertTrue(app.appStart());
	}
	
	@Test
	void convertCSVtoJSONLocalTest() {
		System.out.println("Teste de converter ficheiro CSV via Local");
		System.out.println("Introduzir 1");
		System.out.println("Deve Introduzir: " + k + "\\src\\main\\resources\\horario-exemplo.csv");
		assertTrue(app.appStart());
	}
	
	@Test
	void convertJSONtoCSVURLLocal() {
		System.out.println("Teste de converter ficheiro JSON via Local");
		System.out.println("Introduzir 2");
		System.out.println("Deve Introduzir: " + k + "\\src\\main\\resources\\output.csv");
		assertTrue(app.appStart());
	}
}
