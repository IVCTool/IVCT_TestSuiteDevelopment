/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

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


package de.fraunhofer.iosb.tc_lib_helloworld;

import org.slf4j.Logger;

public class CountryValues {

  private final String countryName;
  private float prevPopulation = 0;
  private float currPopulation = 0;

  public CountryValues(final String name) {
    this.countryName = name;
  }

  @Override
  public String toString() {
    return "(" + this.countryName + ", " + this.currPopulation + ")";
  }

  /**
   * @return the current population value
   */
  public float getPopulation() {
    return this.currPopulation;
  }

  /**
   * @param population
   *          the new population value
   */
  public void setPopulation(final float population) {
    this.prevPopulation = this.currPopulation;
    this.currPopulation = population;
  }

  /**
   * @param delta
   *          the factor the population increases
   * @param logger
   *          reference to the logger
   * @return true when NOT within the test range
   */
  public boolean testPopulation(final float delta, final Logger logger) {
    final float rate = currPopulation / prevPopulation;

    logger.info("testPopulation: population counter received: {} (observed growing rate = {}, allowed growth rate: {})", this.currPopulation, rate, delta);
    if (rate <= delta) {
      return false;
    }
    logger.warn("---------------------------------------------------------------------");
    logger.warn("testPopulation FAILED");
    logger.warn("---------------------------------------------------------------------");

    return true;
  }
  
  
  // get und Set Methoden

  public float getPrevPopulation() {
    return prevPopulation;
  }

  public String getCountryName() {
    return countryName;
  }  
  

}

