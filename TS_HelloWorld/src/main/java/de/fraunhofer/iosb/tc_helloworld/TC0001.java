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

package de.fraunhofer.iosb.tc_helloworld;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_LoggingFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import de.fraunhofer.iosb.tc_lib_helloworld.HelloWorldBaseModel;
import de.fraunhofer.iosb.tc_lib_helloworld.HelloWorldTcParam;
import hla.rti1516e.FederateHandle;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mul (Fraunhofer IOSB)
 */
public class TC0001 extends AbstractTestCase {
    private String                              federateName                   = "IVCT";
    FederateHandle                              federateHandle;

    // Build test case parameters to use
    static HelloWorldTcParam              helloWorldTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti;
    static HelloWorldBaseModel            helloWorldBaseModel;

    static IVCT_LoggingFederateAmbassador ivct_LoggingFederateAmbassador;

    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {
        helloWorldTcParam              = new HelloWorldTcParam(tcParamJson);
    	ivct_rti             = IVCT_RTI_Factory.getIVCT_RTI(logger);
    	helloWorldBaseModel          = new HelloWorldBaseModel(logger, ivct_rti, helloWorldTcParam);
    	ivct_LoggingFederateAmbassador = new IVCT_LoggingFederateAmbassador(helloWorldBaseModel, logger);
    	return helloWorldBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        stringBuilder.append("TEST PURPOSE\n");
        stringBuilder.append("Test if a HelloWorld federate calculates a fixed population increase\n");
        stringBuilder.append("correctly\n");
        stringBuilder.append("Observe the federate for a fixed number of cycles and compare the\n");
        stringBuilder.append("last received value with the previously received value plus the fixed\n");
        stringBuilder.append("percentage and a small tolerence for each cycle\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        final String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }

    public void displayOperatorInstructions(final Logger logger) {
        String s = new String();
        s = "\n"
        +   "---------------------------------------------------------------------\n"
        +   "OPERATOR INSTRUCTIONS: Start the test federate before starting the test case with the same\n"
        +   "OPERATOR INSTRUCTIONS: federate name as in the TcParam.json file\n"
        +   "OPERATOR INSTRUCTIONS: The federate should run for the full duration of all the HelloWorld\n"
        +   "OPERATOR INSTRUCTIONS: tests\n"
        +   "---------------------------------------------------------------------\n";

        logger.info(s);
    }


    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

        // Notify the operator
        displayOperatorInstructions(logger);

        // Initiate rti
        this.federateHandle = helloWorldBaseModel.initiateRti(this.federateName, ivct_LoggingFederateAmbassador);

        // Do the necessary calls to get handles and do publish and subscribe
        if (helloWorldBaseModel.init()) {
            throw new TcInconclusive("Cannot helloWorldBaseModel.init()");
        }
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

        // Allow time to work and get some reflect values.
        if (helloWorldBaseModel.sleepFor(helloWorldTcParam.getSleepTimeWait())) {
            throw new TcInconclusive("sleepFor problem");
        }

        // Loop a number of cycles and test whether the population increases according to the conformance statement
        for (int i = 0; i < 10; i++) {

            // Check if a hello world message has arrived
            if (helloWorldBaseModel.getReflectMessageStatus()) {
                throw new TcInconclusive("Did not receive any Population message");
            }

            // Test the population increase based on the previous and the current values within a percent range tolerance
            if (helloWorldBaseModel.testCountryPopulation(getSutFederateName(), helloWorldTcParam.getPopulationGrowthValue())) {
                throw new TcFailed("Population incorrectly calculated");
            }

            // Allow for some time to pass
            if (helloWorldBaseModel.sleepFor(helloWorldTcParam.getSleepTimeCycle())) {
                throw new TcInconclusive("sleepFor problem");
            }

            sendTcStatus ("running", i*10);
        }
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive {
        // Terminate rti
        helloWorldBaseModel.terminateRti();
    }
}
