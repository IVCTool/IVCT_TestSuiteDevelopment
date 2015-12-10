package de.fraunhofer.iosb.tc_lib_helloworld;

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import java.net.URL;


/**
 * Store test case parameters
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class HelloWorldTcParam implements IVCT_TcParam {
    // Get test case parameters
    //      use some constants for this example till we get params from a file
    private final String federation_name    = "HelloWorld";
    private final String rtiHost            = "localhost";
    private final String settingsDesignator = "crcAddress=" + this.rtiHost;
    private final int    fileNum            = 1;
    private URL[]        urls               = new URL[this.fileNum];
    private final String basePath           = "build/resources/main/";
    private long         sleepTimeCycle     = 1000;
    private long         sleepTimeWait      = 3000;
    private final String sutFederate        = "A";


    public HelloWorldTcParam() {
        this.urls[0] = this.getClass().getClassLoader().getResource("HelloWorld.xml");

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
