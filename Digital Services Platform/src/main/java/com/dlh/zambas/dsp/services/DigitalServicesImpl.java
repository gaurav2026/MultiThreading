package com.dlh.zambas.dsp.services;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import com.dlh.zambas.backend.operation.ITokenOperations;
import com.dlh.zambas.backend.operation.TokenOperationsImpl;
import com.dlh.zambas.dsp.constant.DSPConstants;
import com.dlh.zambas.dsp.hibernate.DatabaseOperationsImpl;
import com.dlh.zambas.dsp.hibernate.IDatabaseOperations;
import com.dlh.zambas.dsp.pojo.DSPPojo;
import com.dlh.zambas.mdw.logger.AppLog;
import com.dlh.zambas.utils.ConnectionFactory;
import com.dlh.zambas.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * class gets called by controller package to fetch ouath2 token
 * @author singhg
 *
 */
public class DigitalServicesImpl implements IDigitalServices {

	private JsonObject jsonObject = null;
	private Date systemDateTime = null;
	private Date lastUpdatedDateTime = null;
	private IDatabaseOperations dbOperations = null;
	private AppLog appLog = AppLog.getAppLog();
	private int crNumber;

	/**
	 * check if header params are missing
	 * call ouath2 server as well as database layer
	 */
	public Response fetchDetailsFromStarBackend(
			int crNumber) throws Exception {

		try {
			// sslContext in set for TLS1.2 enforcement
			ConnectionFactory.getSslContext();
			AppLog.enableLogger(true);
			if (crNumber != 0) {
				this.crNumber = crNumber;
				appLog.info("CR Number : " + crNumber);
				return Response.ok(returnAccessToken()).build();
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(DSPConstants.ERROR_DESCRIPTION.value(),
						"Mandatory header params are missing");
				return Response.ok(new Gson().toJson(jsonObject)).build();
			}

		} catch (Exception e) {
			appLog.error("Exception thrown : ", e);

			if (e.getMessage()
					.equalsIgnoreCase(DSPConstants.DB_FAILURE.value())) {
				/**
				 * To not stop functional flow for any issue like insertion
				 * failed and any other DB exception
				 */
				ITokenOperations tokenOperations = new TokenOperationsImpl();
				return Response.ok(
						new Gson().toJson(tokenOperations
								.fetchValidAccessToken(
										crNumber).getToken())).build();
			} else {
				return Response.ok(e.getMessage()).build();
			}

		}
	}

	/**
	 * returns appropriate response
	 * 
	 * @return
	 * @throws Exception
	 */
	private String returnAccessToken() throws Exception {

		String accessToken = null;
		jsonObject = new JsonObject();

		dbOperations = new DatabaseOperationsImpl();
		DSPPojo pojo = dbOperations.returnDBValues(crNumber);

		/**
		 * if there is no entry in DB. This block gets executed only once , for
		 * first service call
		 */
		if (null == pojo) {
			dbOperations.insertIntoDB(crNumber);
			pojo = dbOperations.returnDBValues(crNumber);
		}

		lastUpdatedDateTime = Utility.timeInGMTTimeZone(pojo
				.getLastModifiedTime());
		systemDateTime = Utility.timeInGMTTimeZone(new Date().toString());

		long timeDifference = systemDateTime.getTime()
				- lastUpdatedDateTime.getTime();
		
		/**
		 * checks whether time difference is greater than token expiration time
		 * If yes, then DB entries get updated and access token is returned. If
		 * no, access token is returned
		 */
		if (TimeUnit.MILLISECONDS.toSeconds(timeDifference) >= pojo
				.getExpirationTime()) {
			accessToken = dbOperations.updateEntries(pojo);
		} else {
			accessToken = pojo.getToken();
		}

		jsonObject.addProperty(DSPConstants.ACCESS_TOKEN.value(), accessToken);
		appLog.info("Access token : " + jsonObject);
		return jsonObject.toString();

	}

}
