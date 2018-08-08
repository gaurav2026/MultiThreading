package com.dlh.zambas.backend.operation;

import com.dlh.zambas.dsp.pojo.DSPPojo;

public interface ITokenOperations {

	DSPPojo fetchValidAccessToken(int crNumber)
			throws Exception;
}
