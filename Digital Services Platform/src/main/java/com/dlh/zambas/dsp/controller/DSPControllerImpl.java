package com.dlh.zambas.dsp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import com.dlh.zambas.dsp.services.DigitalServicesImpl;
import com.dlh.zambas.dsp.services.IDigitalServices;
import com.google.gson.JsonObject;

/**
 * 
 * This class gets called from AI WebServicePluginInterceptor plugin. It fetches
 * a valid ouath2 token.
 *
 */
@CrossOriginResourceSharing(allowAllOrigins = true)
public class DSPControllerImpl implements IDSPController {

	/**
	 * 
	 * AI plugin would call this method
	 * 
	 * @param tokenResourceURI
	 *            :- URI from where Ouath token gets feched
	 * @param crNumber
	 *            :- 1235
	 * 
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/baggage/token")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response fetchDetailsFromInterceptor(
			@HeaderParam("crNumber") int crNumber) throws Exception {
		IDigitalServices digitalServices = new DigitalServicesImpl();
		return digitalServices.fetchDetailsFromStarBackend(crNumber);
	}

	@GET
	@Path("/bagJourney")
	@Produces(MediaType.APPLICATION_JSON)
	public String fetchDetails(@QueryParam("trackingId") String trackingID,
			@QueryParam("bagTagNumber") String bagTagNumber,
			@QueryParam("outboundDate") String outboundDate,
			@QueryParam("paxName") String paxName) throws Exception {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("message", "Welcome to dummy world !!! ");
		Thread.sleep(300000);
		return jsonObject.toString() ;
	}

}
