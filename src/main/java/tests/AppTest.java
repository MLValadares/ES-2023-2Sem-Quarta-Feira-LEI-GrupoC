package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.App;

import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;

class AppTest {
	private App app;

	@BeforeEach
	void setUp() throws Exception {
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
		System.out.println("Deve Introduzir: C:\\Users\\Miguel\\git\\ES-2023-2Sem-Quarta-Feira-LEI-GrupoC\\src\\main\\resources\\horario-exemplo.csv");
		assertTrue(app.appStart());
	}
	
	@Test
	void convertJSONtoCSVURLLocal() {
		System.out.println("Teste de converter ficheiro JSON via Local");
		System.out.println("Introduzir 2");
		System.out.println("Deve Introduzir: C:\\Users\\Miguel\\git\\ES-2023-2Sem-Quarta-Feira-LEI-GrupoC\\src\\main\\resources\\output.csv");
		assertTrue(app.appStart());
	}
	
//	@Test
//	void getOption1Test() {
//		System.out.println("Pressionar opcao 1");
//		assertEquals(app.getFlowOption(),1);
//	}
//	
//	@Test
//	void getOption2Test() {
//		System.out.println("Pressionar opcao 2");
//		assertEquals(app.getFlowOption(),2);
//	}
	
//	@Test
//	void getOptionExceptionTest() {
//		System.out.println("Pressionar opcao a");
//		assertThrows(Exception.class, () -> app.getFlowOption());
//	}
	
//	@Test
//	void convertCSVFromURL() {
//		String str = "https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv";
//	}
	
	
//	@Test
//	void getInputStreamURLTest() {
//		System.out.println("Teste de obter ficheiro via URL");
//		System.out.println("Deve Introduzir: https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv");
//		InputStream inputStream = null;
//		assertNotSame(app.getInputStream(),inputStream);
//	}
	
//	@Test
//	void getInputStreamFileNotFoundException() {
//		System.out.println("Teste MalformedURLException");
//		System.out.println("a");
//		Throwable exception = assertThrows(MalformedURLException.class, () -> app.getInputStream());
////		InputStream inputStream = null;
////		assertNotSame(app.getInputStream(),inputStream);
//	}
}
