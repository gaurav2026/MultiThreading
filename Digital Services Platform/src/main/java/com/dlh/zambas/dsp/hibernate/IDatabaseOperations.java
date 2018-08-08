package com.dlh.zambas.dsp.hibernate;

import com.dlh.zambas.dsp.pojo.DSPPojo;
import com.dlh.zambas.dsp.pojo.OauthDBPojo;

public interface IDatabaseOperations {

	DSPPojo returnDBValues(int crNumber) throws Exception;

	void insertIntoDB(int crNumber) throws Exception;

	String updateEntries(DSPPojo pojo) throws Exception;

	OauthDBPojo fetchOauthDetails(int crNumber) throws Exception;
}
