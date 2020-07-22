package de.fraunhofer.iosb.tc_lib_helloworld;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CountryValuesTest {

	final Logger log = LoggerFactory.getLogger(CountryValuesTest.class);

	@Test
	void test() {
		CountryValues cv = new CountryValues("TestCountry");
		cv.setPopulation(1.0f);
		cv.setPopulation(1.01f);
		assertTrue("Assert to discover wrong rate", cv.testPopulation(.01f, log));
		assertFalse("Assert to acknowledge correct rate", cv.testPopulation(1.01f, log));
	}

}
