package de.fraunhofer.iosb.tc_lib_helloworld_Tests;


// import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_helloworld.CountryValues;

class CountryValuesTest {

	final Logger log = LoggerFactory.getLogger(CountryValuesTest.class);


	@Test
	void test() {
		CountryValues cv = new CountryValues("TestCountry");
		cv.setPopulation(1.0f);
		cv.setPopulation(1.01f);
		assertTrue(cv.testPopulation(0.01f, log));
		assertFalse(cv.testPopulation(1.01f, log));
	}

}
