package com.dlh.zambas.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * unit test using RestAssured
 * @author singhg
 *
 */
public class OuathTokenTest extends ConnectivityTest {

	@Test
	public void successScenarioForToken() {
		Response response = RestAssured
				.given()
				.when()
				.header("tokenUri",
						"https://staralliance-nonprod-qa.apigee.net/api/authorization/v1/clientcredentials?grant_type=client_credentials")
				.header("crNumber", 2135).get("/baggage/token");
		System.out.println(response.getBody().asString());
	}

	@Test
	public void messageWhenTokenUriIsMissing() {
		RestAssured
				.given()
				.when()
				.header("tokenUri", "")
				.header("crNumber", 2135)
				.get("/baggage/token")
				.then()
				.assertThat()
				.body("message",
						Matchers.equalToIgnoringCase("Mandatory header params are "));
	}

	@Test
	public void messageWhenCrNumberIsMissing() {
		RestAssured
				.given()
				.when()
				.header("tokenUri",
						"https://staralliance-nonprod-qa.apigee.net/api/authorization/v1/clientcredentials?grant_type=client_credentials")
				.get("/baggage/token")
				.then()
				.assertThat()
				.body("message",
						Matchers.equalToIgnoringCase("Mandatory header params are missing"));
	}
	
	@Test
	public void messageWhenWrongClientIdUsed(){
		Response response = RestAssured
				.given()
				.when()
				.header("tokenUri",
						"https://staralliance-nonprod-qa.apigee.net/api/authorization/v1/clientcredentials?grant_type=client_credentials")
				.header("crNumber", 2135).get("/baggage/token");
		System.out.println(response.getBody().asString());
	}
	
	@Test
	public void errorCodeWhenWrongClientIdUsed(){
		Response response = RestAssured
				.given()
				.when()
				.header("tokenUri",
						"https://staralliance-nonprod-qa.apigee.net/api/authorization/v1/clientcredentials?grant_type=client_credentials")
				.header("crNumber", 2135).get("/baggage/token");
		System.out.println(response.getStatusCode());
	}
	
	
}
