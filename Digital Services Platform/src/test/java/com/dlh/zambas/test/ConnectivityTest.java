package com.dlh.zambas.test;

import io.restassured.RestAssured;

import org.junit.BeforeClass;

public class ConnectivityTest {

	 @BeforeClass
	    public static void setup() {
	        String port = System.getProperty("server.port");
	        if (port == null) {
	            RestAssured.port = Integer.valueOf(9095);
	        }
	        else{
	            RestAssured.port = Integer.valueOf(port);
	        }


	        String basePath = System.getProperty("server.base");
	        if(basePath==null){
	            basePath = "/selfservice/dsp-api/v1.0/";
	        }
	        RestAssured.basePath = basePath;

	        String baseHost = System.getProperty("server.host");
	        if(baseHost==null){
	            baseHost = "http://localhost";
	        }
	        RestAssured.baseURI = baseHost;

	    }


}
