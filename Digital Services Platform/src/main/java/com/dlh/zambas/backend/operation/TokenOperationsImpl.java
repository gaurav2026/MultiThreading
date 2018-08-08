package com.dlh.zambas.backend.operation;

import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import com.dlh.zambas.dsp.constant.DSPConstants;
import com.dlh.zambas.dsp.hibernate.DatabaseOperationsImpl;
import com.dlh.zambas.dsp.hibernate.IDatabaseOperations;
import com.dlh.zambas.dsp.pojo.DSPPojo;
import com.dlh.zambas.dsp.pojo.OauthDBPojo;
import com.dlh.zambas.dsp.pojo.OauthFailurePojo;
import com.dlh.zambas.dsp.pojo.OauthSuccessPojo;
import com.dlh.zambas.mdw.logger.AppLog;
import com.dlh.zambas.utils.ConnectionFactory;
import com.dlh.zambas.utils.Utility;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

/**
 * Class calls oauth2 server and fetches ouath2 details
 * 
 * @author singhg
 *
 */
public class TokenOperationsImpl implements ITokenOperations {

	private Client client = null;
	private WebResource webResource = null;
	private ClientResponse clientResponse = null;
	private DSPPojo pojo = null;
	private AppLog appLog = AppLog.getAppLog();
	private URLConnectionClientHandler clientHandler = null;

	/**
	 * stores values in Pojo to insert these details in DB
	 * 
	 * @param clientResponse
	 * @return
	 * @throws ParseException
	 */

	private DSPPojo setSuccessReturnParameters(OauthSuccessPojo ouathPojo,
			int crNumber) throws ParseException {
		pojo = new DSPPojo();

		appLog.debug("Token : " + ouathPojo.getAccess_token());
		/**
		 * Id is CR Number.
		 */
		pojo.setId(crNumber);
		pojo.setToken(ouathPojo.getAccess_token());
		pojo.setExpirationTime(Integer.parseInt(ouathPojo.getExpires_in()));
		pojo.setLastModifiedTime(Utility.timeInGMTTimeZone(
				new Date().toString()).toString());
		pojo.setStatusCode(200);
		return pojo;
	}

	/**
	 * method gets called for fetching of ouath2 token . These details gets
	 * updated in DB.
	 * 
	 * @param refreshToken
	 * @param starDSPURI
	 * @return
	 * @throws Exception
	 */
	public DSPPojo fetchValidAccessToken(int crNumber)
			throws Exception {
		pojo = new DSPPojo();
		OauthDBPojo ouathDBPojo = fetchOuathServerDetailsFromDB(crNumber);

		/**
		 * code is commented because we don't have any clarity on proxy yet.
		 * GOPS will let us know about proxy
		 */
		clientHandler = new URLConnectionClientHandler(new ConnectionFactory(
				ouathDBPojo.getTokenUri()));
		client = new Client(clientHandler);

		 //client = Client.create();

		webResource = client.resource(ouathDBPojo.getTokenUri());

		clientResponse = webResource.header(DSPConstants.CONTENT_TYPE.value(),
				MediaType.APPLICATION_FORM_URLENCODED).post(
				ClientResponse.class, payLoadForOuathToken(ouathDBPojo));

		appLog.debug("Client response while fetching ouath token using refresh token :"
				+ clientResponse.getStatus());

		if (clientResponse.getStatus() == 200) {
			OauthSuccessPojo ouathPojo = new Gson().fromJson(
					clientResponse.getEntity(String.class),
					OauthSuccessPojo.class);
			pojo = setSuccessReturnParameters(ouathPojo, crNumber);
		} else {
			OauthFailurePojo ouathFailurePojo = new Gson().fromJson(
					clientResponse.getEntity(String.class),
					OauthFailurePojo.class);
			pojo = setFailureReturnParameters(ouathFailurePojo);
		}
		return pojo;
	}

	/**
	 * json response in case of failed scenarios
	 * 
	 * @param ouathFailurePojo
	 * @return
	 */
	private DSPPojo setFailureReturnParameters(OauthFailurePojo ouathFailurePojo) {
		pojo = new DSPPojo();
		pojo.setFailureMessage(ouathFailurePojo.getMessage());
		pojo.setStatusCode(Integer.parseInt(ouathFailurePojo.getCode()));
		return pojo;
	}

	private OauthDBPojo fetchOuathServerDetailsFromDB(int crNumber)
			throws Exception {
		IDatabaseOperations databaseOperations = new DatabaseOperationsImpl();
		return databaseOperations.fetchOauthDetails(crNumber);
	}

	/**
	 * payload to fetch oauth2 token
	 * 
	 * @param crNumber
	 * @return
	 * @throws Exception
	 */
	private String payLoadForOuathToken(OauthDBPojo ouathDBPojo)
			throws Exception {
		return "client_id=" + ouathDBPojo.getClientId() + "&client_secret="
				+ ouathDBPojo.getClientSecret();
	}
}