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

    CountryValues ctval1 = new CountryValues("Country_1");
    LOGGER_TestHelloBaseModel.info("Country Name: " + ctval1);
    
    // set the basic population value
    ctval1.setPopulation(2000.0F);
        
    populationReport(1, ctval1);               // show us curent and previous Population

    
    // Population increases 1/1000
    ctval1.setPopulation(2002.0F);
    
    //ctval1.setPopulation(2032.0F);          // with this Number the test will fail
    

    populationReport(2, ctval1);               // show us curent and previous Population

    // Test with delta 1/1000    
    if (!ctval1.testPopulation(1.001F, LOGGER_TestHelloBaseModel)) {
      LOGGER_TestHelloBaseModel.info("Increase of population in expected range \n");
    }

    // CountryValues.testPopulation @return true when NOT within the test range
    assertFalse("Population not Range ", (ctval1.testPopulation(1.001F, LOGGER_TestHelloBaseModel)));

  }

  @Test
  public void testSetPopulationNotInRange() {
    LOGGER_TestHelloBaseModel.info("\n");
    LOGGER_TestHelloBaseModel.info("---------------------------------------------------------------------");
    LOGGER_TestHelloBaseModel.info("Test  testSetPopulationNotInRange()");

    CountryValues ctval1 = new CountryValues("Country_2");
    LOGGER_TestHelloBaseModel.info("Country Name: " + ctval1);

    ctval1.setPopulation(5000.0F);
    populationReport(1, ctval1);
 
    // Population increases 1/1000
    //ctval1.setPopulation(5005.0F);            // whith this the test should fail
    
    // Population increases 20%
    ctval1.setPopulation(6000.0F);
    

    populationReport(2, ctval1);

    // Test with delta 1/1000    
    if (ctval1.testPopulation(1.001F, LOGGER_TestHelloBaseModel)) {
      LOGGER_TestHelloBaseModel.info("As planned increase of population not in range\n");
    }


    // CountryValues.testPopulation @return true when NOT within the test range
    assertTrue("Population growth unexpectedly in range", (ctval1.testPopulation(1.001F, LOGGER_TestHelloBaseModel)));

  }

  private void populationReport(int call, CountryValues ctval1) {
    LOGGER_TestHelloBaseModel.info("current  population(" + call + "): " + ctval1.getPopulation());
    LOGGER_TestHelloBaseModel.info("previous population(" + call + "): " + ctval1.getPrevPopulation());
  }

}
