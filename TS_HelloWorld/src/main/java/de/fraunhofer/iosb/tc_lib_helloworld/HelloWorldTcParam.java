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

import java.net.URL;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private long         sleepTimeWait      = 4000;
    private String sutFederate;


    public HelloWorldTcParam(final String paramJson) throws TcInconclusive {
    	// paramJson are not needed for this test suite

    	// get FOM model
		this.urls[0] = this.getClass().getClassLoader().getResource("HelloWorld.xml");
    }


    /**
     * @return the RTI host value
     */
    public float getPopulationGrowthValue() {
        return 1.0003f;
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
