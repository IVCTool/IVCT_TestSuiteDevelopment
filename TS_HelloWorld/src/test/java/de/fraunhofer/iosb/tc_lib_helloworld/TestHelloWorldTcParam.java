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

package de.fraunhofer.iosb.tc_lib_helloworld;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import nato.ivct.commander.Factory;

public class TestHelloWorldTcParam {

	final Logger LOGGER_HWTP = LoggerFactory.getLogger(TestHelloWorldTcParam.class);

	public static final String IVCT_CONF = "IVCT_CONF";

	public Properties props = Factory.props;

	/*
	 * to Test HelloWorldTcParam we have to initialize a object. For this we need *
	 * 'String paramJson' and 'Properties props'
	 *
	 * String paramJson is read out of the Json-Parameter file this can be done by
	 * Factory.readWholefile which is normally startet by CmdStartTc.execute
	 *
	 * for the 'Properties props' we have to read in the properties File
	 * IVCT.properties which is normally read from Factory.java.initialize();
	 */



	@Test
	public void testTcParam () throws TcInconclusive
	{
		String jsonString = Factory.readWholeFile(System.getProperty("user.dir") + "/src/main/resources/TcParam.json");
		HelloWorldTcParam tcParam = new HelloWorldTcParam(jsonString);
		assertTrue(tcParam.getSleepTimeWait() > 0);
		assertTrue(tcParam.getPopulationGrowthValue() > 0);
		assertTrue(tcParam.getSleepTimeCycle() > 0);
		assertTrue(tcParam.getUrls().toString().length() > 0);
	}


	// Find the JsonFile and read it
	@Test
	public void testReadOutJsonFile() throws FileNotFoundException {
		LOGGER_HWTP.info("");
		LOGGER_HWTP.info("-------------------------------");
		LOGGER_HWTP.info("Test testReadOutJsonFile() : ");

		// LOGGER_HWTP.info("Working-Dir: " + System.getProperty("user.dir") ); //debug

		File tempJson = new File(System.getProperty("user.dir") + "/src/main/resources/TcParam.json");

		LOGGER_HWTP.info("TcParam.json-File absolutPath:  " + tempJson.getAbsolutePath()); // debug

		if (tempJson.exists()) {
			Scanner sc1 = new Scanner(tempJson);
			LOGGER_HWTP.info("System is finding the file");

			while (sc1.hasNextLine()) {
				LOGGER_HWTP.info(sc1.nextLine());
			}
			sc1.close();
		} else {
			LOGGER_HWTP.info("The system is nof finding the file ! ");
		}

		assertTrue("JsonFile is not readable", tempJson.exists());

	}

	/*
	 * Find the property-File and read it the path for the property-File
	 * IVCT.properties is found by a variable IVCT_CONF which is normaly set to
	 * OS-environment or by a startscript. fi. :
	 * " export IVCT_CONF=/opt/MSG134/IVCT_Runtime "
	 */

	@Test
	public void findAndReadPropertyFile() throws IOException, FileNotFoundException {
		LOGGER_HWTP.info("");
		LOGGER_HWTP.info("-------------------------------");
		LOGGER_HWTP.info("Test  findAndReadPropertyFile() : ");

		String home = "";
		String confPath = System.getProperty("user.dir") + "/src/main/resources";
		Properties props1 = new Properties(props);

		// if IVCT_CONF is not set in OS-environment, we do this in our environment
		if (System.getenv(IVCT_CONF) == null) {
			System.setProperty(IVCT_CONF, confPath);
			home = System.getProperty(IVCT_CONF);
			LOGGER_HWTP.info("We get IVCT_CONF from our System : " + System.getProperty(IVCT_CONF)); // debug
		} else {
			home = System.getenv(IVCT_CONF);
			LOGGER_HWTP.info("We get  IVCT_CONF from the OS-Environment : " + System.getenv(IVCT_CONF)); // debug
		}

		FileReader readIt = new FileReader(home + "/IVCT.properties");

		props1.load(readIt);

		LOGGER_HWTP.info("props1.getProperty IVCT_SUT_HOME_ID  : " + props1.getProperty("IVCT_SUT_HOME_ID"));

		// assertTrue("getProperty(\"IVCT_SUT_HOME_ID\") is empty ",
		// props1.getProperty("IVCT_SUT_HOME_ID") != null );
		assertFalse("getProperty(\"IVCT_SUT_HOME_ID\") is empty ", props1.getProperty("IVCT_SUT_HOME_ID").isEmpty());
	}

	@Test
	public void testHelloWorldTcParam() throws Exception {
		LOGGER_HWTP.info("");
		LOGGER_HWTP.info("-------------------------------");
		LOGGER_HWTP.info("Test creating TcParam object");

		String tcParamString = "{\r\n" +
				"  \"growthRate\" : \"1.0003\",\r\n" +
				"  \"SOMfile\":\"$(IVCT_SUT_HOME_ID)/$(IVCT_SUT_ID)/$(IVCT_TESTSUITE_ID)/HelloWorld.xml\"\r\n" +
				"}";

		HelloWorldTcParam tcParam = new HelloWorldTcParam(tcParamString);
		LOGGER_HWTP.info("number of FOM files found: {}", tcParam.getUrls().length);
		assertTrue(tcParam.getUrls().length > 0);
		for (int i=0; i<tcParam.getUrls().length; i++) {
			LOGGER_HWTP.info("FOM File: {}", tcParam.getUrls()[i]);
		}

	}
}
