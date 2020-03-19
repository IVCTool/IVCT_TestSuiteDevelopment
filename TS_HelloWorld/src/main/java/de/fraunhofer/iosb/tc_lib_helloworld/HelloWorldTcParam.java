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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

/**
 * Store test case parameters
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class HelloWorldTcParam implements IVCT_TcParam {
    // Get test case parameters
    //      use some constants for this example till we get params from a file
    private final int    fileNum            = 1;
    private URL[]        urls               = new URL[this.fileNum];
    private long         sleepTimeCycle     = 1000;
    private long         sleepTimeWait      = 5000;
    private float growthRate = 1.03f;
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HelloWorldTcParam.class);


    public HelloWorldTcParam(final String paramJson) throws TcInconclusive {
    	// paramJson are not needed for this test suite

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
			jsonObject = (JSONObject) jsonParser.parse(paramJson);

			// get a String from the JSON object
			String growthRateStr =  (String) jsonObject.get("growthRate");
			if (growthRateStr == null) {
                throw new TcInconclusive("The key <growthRate>  was not found");
			}
			growthRate = Float.parseFloat(growthRateStr);
			
	    	// get FOM model
			String somFile =  (String) jsonObject.get("SOMfile");
			if (somFile == null) {
                throw new TcInconclusive("The key <SOMfile> was not found");
			}
			urls[0] = (new File(somFile)).toURI().toURL();

		} catch (ParseException e1) {
            throw new TcInconclusive("Error in parsing FOM file: " + e1.toString());
		} catch (MalformedURLException e) {
            throw new TcInconclusive("The key <growthRate> containes malformed URL");
		}
    }


    /**
     * @return the RTI host value
     */
    public float getPopulationGrowthValue() {
        return growthRate;
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
     * @return the urls
     */
    @Override
    public URL[] getUrls() {
        return this.urls;
    }
}
