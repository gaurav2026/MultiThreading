package com.dlh.zambas.dsp.controller;

import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

@CrossOriginResourceSharing(allowAllOrigins = true)
public interface IDSPController {

	 Response fetchDetailsFromInterceptor(int crNumber) throws Exception;
}
