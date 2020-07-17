package de.fraunhofer.iosb.tc_helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TSTest {

	Logger runLogger = LoggerFactory.getLogger(TSTest.class);
	String tsName = "TS_HelloWord";
	String federationName = "HelloWorld";
	String sutName = "A";
	String tcParamJson = "{ \"growthRate\" : \"1.0003\", \"SOMfile\":  \"HelloWorld.xml\" }";
	String settingsDesignator = "(setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")";

}


