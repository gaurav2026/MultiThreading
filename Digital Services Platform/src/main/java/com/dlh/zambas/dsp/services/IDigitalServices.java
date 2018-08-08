package com.dlh.zambas.dsp.services;

import javax.ws.rs.core.Response;

public interface IDigitalServices {

	 Response fetchDetailsFromStarBackend(int crNumber) throws Exception;
}
