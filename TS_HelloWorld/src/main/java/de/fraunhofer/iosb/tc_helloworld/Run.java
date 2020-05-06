package de.fraunhofer.iosb.tc_helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

public class Run {

	public static void main(String[] args) {
		final Logger runLogger = LoggerFactory.getLogger(Run.class);
		AbstractTestCase[] testSchedule = new AbstractTestCase[2];
		testSchedule[0] = new TC0001();
		testSchedule[1] = new TC0002();

		// initialize
		String tsName = "TS_HelloWord";
		String federationName = "HelloWorld";
		String sutName = "A";
		String tcParamJson = "{ \"growthRate\" : \"1.0003\", \"SOMfile\":  \"HelloWorld.xml\" }";
		String settingsDesignator = "(setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")";

		int i = 0;
		while (i < testSchedule.length) {
			testSchedule[i].setSettingsDesignator(settingsDesignator);
			testSchedule[i].setFederationName(federationName);
			testSchedule[i].setSutName(sutName);
			testSchedule[i].setTcName(testSchedule[i].getClass().getName());
			testSchedule[i].setTsName(tsName);

			testSchedule[i].setSkipOperatorMsg(true);
			IVCT_Verdict verdict = testSchedule[i].execute(tcParamJson, runLogger);
			runLogger.info(verdict.toString());
			i++;
		}

	}

}
