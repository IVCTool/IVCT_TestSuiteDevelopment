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

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Store test case parameters
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class HelloWorldTcParam implements IVCT_TcParam {
    // Get test case parameters
    //      use some constants for this example till we get params from a file
    private String federation_name;
    private String rtiHost;
    private String settingsDesignator;
    private final int    fileNum            = 1;
    private URL[]        urls               = new URL[this.fileNum];
    private final String basePath           = "build/resources/main/";
    private long         sleepTimeCycle     = 1000;
    private long         sleepTimeWait      = 3000;
    private String sutFederate;


    public HelloWorldTcParam(final String paramJson) throws TcInconclusive {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(paramJson);
			// get a String from the JSON object
			federation_name =  (String) jsonObject.get("federationName");
			if (federation_name == null) {
                throw new TcInconclusive("The key \"federationName\" was not found");
			}
			// get a String from the JSON object
			rtiHost =  (String) jsonObject.get("rtiHostName");
			if (rtiHost == null) {
                throw new TcInconclusive("The key \"rtiHostName\" was not found");
			}
			settingsDesignator = "crcAddress=" + this.rtiHost;
			// get a String from the JSON object
			sutFederate =  (String) jsonObject.get("sutFederateName");
			if (sutFederate == null) {
                throw new TcInconclusive("The key \"sutFederateName\" was not found");
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        this.fddFiles[0] = new File(this.basePath + "HelloWorld.xml");
        for (int i = 0; i < this.fileNum; i++) {
            try {
                this.urls[i] = this.fddFiles[i].toURI().toURL();
            }
            catch (final MalformedURLException e) {
                throw new TcInconclusive("HelloWorldTcParam constructor: MalformedURLException");
            }
        }
    }


    /**
     * @return the federation name
     */
    @Override
    public String getFederationName() {
        return this.federation_name;
    }


    /**
     * @return the RTI host value
     */
    public float getPopulationGrowthValue() {
        return 1.03f;
    }


    /**
     * @return the RTI host value
     */
    public String getRtiHost() {
        return this.rtiHost;
    }


    /**
     * @return the settings designator
     */
    @Override
    public String getSettingsDesignator() {
        return this.settingsDesignator;
    }


    /**
     * @return value of sleep time for tmr
     */
    public long getSleepTimeCycle() {
        return this.sleepTimeCycle;
    }


    /**
     * @return value of sleep time for tmr
     */
    public long getSleepTimeWait() {
        return this.sleepTimeWait;
    }


    /**
     * @return name of sut federate
     */
    public String getSutFederate() {
        return this.sutFederate;
    }


    /**
     * @return the urls
     */
    @Override
    public URL[] getUrls() {
        return this.urls;
    }
}
