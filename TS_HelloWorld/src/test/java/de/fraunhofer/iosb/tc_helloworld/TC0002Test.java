package de.fraunhofer.iosb.tc_helloworld;

import static org.junit.Assert.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import de.fraunhofer.iosb.tc_lib_if.IVCT_Verdict;

class TC0002Test extends TSTest {
	
	@Test
	@Override
	@EnabledIfEnvironmentVariable(named = "LRC_CLASSPATH", matches = ".*")
	void test() {
		TC0002 testCase = new TC0002();
		setUp(testCase);
		IVCT_Verdict verdict = testCase.execute(tcParamJson, runLogger);
		runLogger.info("Test Case Verdict: {}", verdict);
		assertSame("Test Case shall pass", IVCT_Verdict.Verdict.PASSED, verdict.verdict);		
	}
}
