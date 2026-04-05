package com.techlabs.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTests {

	@Test
	void applicationClassExists() {
		// Verify the main class is loadable
		assertTrue(Application.class.isAnnotationPresent(
				org.springframework.boot.autoconfigure.SpringBootApplication.class));
	}
}
