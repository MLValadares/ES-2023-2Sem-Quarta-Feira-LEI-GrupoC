package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.App;

class AppTest {
	private App app;

	@BeforeEach
	void setUp() throws Exception {
		this.app = new App();
	}

	@Test
	void test() {
		assertTrue(app.appStart());
	}

}
