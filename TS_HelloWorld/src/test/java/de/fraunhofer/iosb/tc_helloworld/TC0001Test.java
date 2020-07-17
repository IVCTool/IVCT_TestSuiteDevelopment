package de.fraunhofer.iosb.tc_helloworld;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

class TC0001Test extends TSTest {

	TC0001 testCase = new TC0001();

	@BeforeEach
	void setUp() {
		testCase.setSettingsDesignator(settingsDesignator);
		testCase.setFederationName(federationName);
		testCase.setSutName("hw_iosb");
		testCase.setSutFederateName(sutName);			
		testCase.setTcName(TC0001.class.getName());
		testCase.setTsName(tsName);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "LRC_CLASSPATH", matches = ".*")
	void test() {
		testCase.setSkipOperatorMsg(true);
		IVCT_Verdict verdict = testCase.execute(tcParamJson, runLogger);
		runLogger.info("Test Case Verdict: {}", verdict);
		assertTrue(verdict.verdict == IVCT_Verdict.Verdict.PASSED);
	}
}
