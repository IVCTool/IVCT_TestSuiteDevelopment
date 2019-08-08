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

import static org.junit.Assert.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.FileInputStream;

import nato.ivct.commander.Factory;

import java.util.Properties;
import java.util.Scanner;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_helloworld.HelloWorldTcParam;

import de.fraunhofer.iosb.tc_lib.TcInconclusive;

public class TestHelloWorldTcParam {

  final Logger LOGGER_HWTP = LoggerFactory.getLogger(TestHelloWorldTcParam.class);

  public static final String IVCT_CONF = "IVCT_CONF";

  public Properties props = Factory.props;

  /*
   * to Test HelloWorldTcParam we have to initialize a object.
   * For this we need    * 'String paramJson' and 'Properties props'
   * 
   * String paramJson is read out of the Json-Parameter file this can be done by
   * Factory.readWholefile which is normally startet by CmdStartTc.execute
   * 
   * for the 'Properties props' we have to read in the properties File
   * IVCT.properties which ist normally read from Factory.java.initialize();
   */

  
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
    
    assertTrue("JsonFile is not readable", tempJson.exists()  );
    
  }
      
  

  /* Find the property-File and read it
     the path for the property-File IVCT.properties is found by a variable  IVCT_CONF
     which is normaly set to OS-environment or by a startscript. fi. : 
     " export IVCT_CONF=/opt/MSG134/IVCT_Runtime "
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
    
    //assertTrue("getProperty(\"IVCT_SUT_HOME_ID\") is empty ", props1.getProperty("IVCT_SUT_HOME_ID") != null );
    assertFalse("getProperty(\"IVCT_SUT_HOME_ID\") is empty ", props1.getProperty("IVCT_SUT_HOME_ID").isEmpty()  );
    

  }

  
  // test the getFederate Function
  @Test
  public void testGetFederateName() throws IOException, TcInconclusive {
    LOGGER_HWTP.info("");
    LOGGER_HWTP.info("-------------------------------");
    LOGGER_HWTP.info("Test testGetFederateName() : ");

    // To initialize HelloWorldTcParam we need a 'String paramJson'
    String paramFileContentString = Factory.readWholeFile("src/main/resources/TcParam.json");

    // LOGGER_HWTP.info("Contents of paramFileContentString: " +paramFileContentString); //Debug

    // for the 'Properties props' we have to read in the properties File
    Properties props1 = new Properties();
    props1.load(new FileInputStream("src/main/resources/IVCT.properties"));

    // 2 tests for Debugging the property-Object
    // props1.list(System.out) ; // list all properties out of IVCT.properties
    // LOGGER_HWTP.info("Output getPropert(IVCT_TS_HOME_ID): " + props1.getProperty("IVCT_TS_HOME_ID") );  //Debug

    // Now we initialize a HelloWorldTcParam object
    HelloWorldTcParam helloWorldTcParam1 = new HelloWorldTcParam(paramFileContentString, props1);

    // We can start testing
    // LOGGER_HWTP.info("Ausgabe helloWorldTcParam1.getFederationName: "+helloWorldTcParam1.getFederationName());
    // LOGGER_HWTP.info("Ausgabe helloWorldTcParam1.getSutFederate():  "+helloWorldTcParam1.getSutFederate());

    // LOGGER_HWTP.info("--------- Test testGetFederateName() ---------- " ); //Debug

    // we await a String , in the moment 'HelloWorld'
    //assertEquals("Die strings don't match", "HelloWorld", helloWorldTcParam1.getFederationName() );

    // or some String
    String hWTcP1_getFedName = helloWorldTcParam1.getFederationName();
    assertFalse("The String given back by testGetFederateName() is empty ", hWTcP1_getFedName.isEmpty());

  }


  @Test
  public void testGetSettingDesignator() throws IOException, TcInconclusive {
 // see explanations in the test testGetFederateName
    LOGGER_HWTP.info("");
    LOGGER_HWTP.info("-------------------------------");
    LOGGER_HWTP.info("Test testGetSettingDesignator() : ");

    String paramFileContentString = Factory.readWholeFile("src/main/resources/TcParam.json");

    Properties props1 = new Properties();
    props1.load(new FileInputStream("src/main/resources/IVCT.properties"));

    HelloWorldTcParam helloWorldTcParam1 = new HelloWorldTcParam(paramFileContentString, props1);

    LOGGER_HWTP.info("Output helloWorldTcParam1.getSettingsDesignator: " + helloWorldTcParam1.getSettingsDesignator());

    // or some String
    String hWTcP1_getSetDesign = helloWorldTcParam1.getSettingsDesignator();
    // assertFalse("The String given back by testGetFederateName() is empty ",
    // hWTcP1_getSetDesign.isEmpty() );
    assertTrue("The String given back by testGetFederateName() != null  ",
        helloWorldTcParam1.getSettingsDesignator() != null);

    assertFalse("The String given back by testGetFederateName() is empty ", hWTcP1_getSetDesign.isEmpty());

  }

}
