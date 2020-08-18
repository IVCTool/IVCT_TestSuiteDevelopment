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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

import java.util.LinkedList;
import java.util.Properties;

/**
 * @author mul (Fraunhofer IOSB)
 */
public class TC0002 extends AbstractTestCase {
    FederateHandle                              federateHandle;
    private String                              federateName                   = "IVCT";
    
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
        stringBuilder.append("Test if a HelloWorld federate answers with: \"Greetings from <country name> to IVCT\"\n");
        stringBuilder.append("upon receiving a \"HelloWorld\" interaction\n");
        stringBuilder.append("Repeat sending the \"HelloWorld\" interaction for several cycles and evaluate\n");
        stringBuilder.append("the interactions received\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        final String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }

    public void displayOperatorInstructions(final Logger logger) throws TcInconclusive {
        String s = new String();
        s = "\n"
        +   "---------------------------------------------------------------------\n"
        +   "OPERATOR INSTRUCTIONS: \n"
        +   "1. Start the test federate "
        +   getSutFederateName()
        +   " and then hit confirm button\n"
        +   "2. The federate should run for the full duration of the tests\n"
        +   "---------------------------------------------------------------------\n";

        logger.info(s);
        
		sendOperatorRequest(s);
		
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

        final String message = "Hello World from " + this.federateName;
       // final String testMessage = "Greetings from " + getSutFederateName() + " to B";
        final String testMessage = "Greetings from " + getSutFederateName() + " to IVCT";
        
        // Create fields to hold values to send
        ParameterHandleValueMap parameters;
        try {
            parameters = ivct_rti.getParameterHandleValueMapFactory().create(2);
        }
        catch (FederateNotExecutionMember | NotConnected ex1) {
            throw new TcInconclusive(ex1.getMessage());
        }

        // Encode the values
        final HLAunicodeString messageEncoderString = ivct_rti.getEncoderFactory().createHLAunicodeString();
        final HLAunicodeString senderEncoderString = ivct_rti.getEncoderFactory().createHLAunicodeString();
        messageEncoderString.setValue(message);
        senderEncoderString.setValue(federateName);

        // Put the values into parameters
        parameters.put(helloWorldBaseModel.getParameterIdText(), messageEncoderString.toByteArray());
        parameters.put(helloWorldBaseModel.getParameterIdSender(), senderEncoderString.toByteArray());

       
        // Loop a number of cycles and test whether the sut answers the hello world call
        for (int i = 0; i < 10; i++) {
        	
        	helloWorldBaseModel.startSavingInteractions();
            // Send the interaction
            try {
                ivct_rti.sendInteraction(helloWorldBaseModel.getMessageId(), parameters, null);
            }
            catch (InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex1) {
                throw new TcInconclusive(ex1.getMessage());
            }

            // Allow for some time to pass
            if (helloWorldBaseModel.sleepFor(helloWorldTcParam.getSleepTimeCycle())) {
                throw new TcInconclusive("sleepFor problem");
            }
            
            helloWorldBaseModel.stopSavingInteractions();
            compareMessage(logger,testMessage);

            sendTcStatus ("running", i*10+5);
        }
        
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive {
        // Terminate rti
        helloWorldBaseModel.terminateRti();
    }
    
    protected void compareMessage(final Logger logger, final String testMessage) throws TcInconclusive, TcFailed
    {
    	 // Get the saved text messages
        LinkedList<String> savedSutTextMessages = helloWorldBaseModel.getSavedSutTextMessages(getSutFederateName());
        if (savedSutTextMessages.isEmpty()) {
            throw new TcInconclusive("Did not receive any Greetings text messages");
        }
        
        boolean messageFound = false;
        
    	// Compare the text messages
        for (String entry : savedSutTextMessages) {
            logger.info(entry);
            // Test the value of the message
            if (entry.equals(testMessage) == true) {
            	messageFound = true;
            }
        }
        if (!messageFound) {
        	 throw new TcFailed("No message of kind " + testMessage + " received.");
        }
        
    }
    
}
