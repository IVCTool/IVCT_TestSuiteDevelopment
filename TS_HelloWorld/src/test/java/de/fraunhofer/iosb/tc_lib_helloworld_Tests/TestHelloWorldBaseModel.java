/*
Copyright 2019, brf (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib_helloworld_Tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import de.fraunhofer.iosb.tc_lib_helloworld.*;

public class TestHelloWorldBaseModel {

  // define a Logger
  final Logger LOGGER_TestHelloBaseModel = LoggerFactory.getLogger(TestHelloWorldBaseModel.class);

  @Test
  public void testSetPopulationInRange() {
    LOGGER_TestHelloBaseModel.info("\n");
    LOGGER_TestHelloBaseModel.info("---------------------------------------------------------------------");
    LOGGER_TestHelloBaseModel.info("Test  testSetPopulationInRange()");

    CountryValues ctyval = new CountryValues("Country_1");
    LOGGER_TestHelloBaseModel.info("Country Name: " + ctyval);

    ctyval.setPopulation(1000.0F);
    populationReport(1, ctyval);

    // Population increases 1/1000
    ctyval.setPopulation(1001.0F);

    populationReport(2, ctyval);

    // Test with delta 1/1000
    
    // if (ctyval.testPopulation(1.001F, LOGGER_TestHelloBaseModel)) {
    // LOGGER_TestHelloBaseModel.info("\nIncrease of population not in expected
    // range\n");
    // }

    if (!ctyval.testPopulation(1.001F, LOGGER_TestHelloBaseModel)) {
      LOGGER_TestHelloBaseModel.info("Increase of population in expected range \n");
    }

    // CountryValues.testPopulation @return true when NOT within the test range
    assertFalse("Population not Range ", (ctyval.testPopulation(1.001F, LOGGER_TestHelloBaseModel)));

  }

  @Test
  public void testSetPopulationNotInRange() {
    LOGGER_TestHelloBaseModel.info("\n");
    LOGGER_TestHelloBaseModel.info("---------------------------------------------------------------------");
    LOGGER_TestHelloBaseModel.info("Test  testSetPopulationNotInRange()");

    CountryValues ctyval = new CountryValues("Country_2");
    LOGGER_TestHelloBaseModel.info("Country Name: " + ctyval);

    ctyval.setPopulation(5000.0F);
    populationReport(1, ctyval);

    // Population increases 20%
    ctyval.setPopulation(6000.0F);

    populationReport(2, ctyval);

    if (ctyval.testPopulation(1.001F, LOGGER_TestHelloBaseModel)) {
      LOGGER_TestHelloBaseModel.info("Increase of population not in expected range\n");
    }


    // CountryValues.testPopulation @return true when NOT within the test range

    assertTrue("Population increases too much, not recognized",
        (ctyval.testPopulation(1.001F, LOGGER_TestHelloBaseModel)));

  }

  private void populationReport(int call, CountryValues ctyval) {
    LOGGER_TestHelloBaseModel.info("current  population:  (" + call + ") " + ctyval.getPopulation());
    LOGGER_TestHelloBaseModel.info("previous population:  (" + call + ") " + ctyval.getPrevPopulation());
  }

}
