package com.evancharlton.mileage.tests;

import static org.junit.Assert.fail;

public abstract class TestCase {
	protected void assertCloseEnough(double expected, double actual) {
		assertCloseEnough(expected, actual, 0.001);
	}

	protected void assertCloseEnough(double expected, double actual, double delta) {
		if (Math.abs(expected - actual) >= delta) {
			fail("Expected <" + expected + " +/- " + delta + "> but was <" + actual + ">");
		}
	}
}
