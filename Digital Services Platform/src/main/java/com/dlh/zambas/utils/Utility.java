package com.dlh.zambas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.dlh.zambas.dsp.constant.DSPConstants;
import com.dlh.zambas.dsp.pojo.DSPPojo;
import com.google.gson.JsonObject;

public class Utility {

	public static Date timeInGMTTimeZone(String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(
				DSPConstants.DATE_FORMAT.value());
		format.setTimeZone(TimeZone.getTimeZone(DSPConstants.GMT.value()));
		return format.parse(time);
	}

	/**
	 * set exception
	 * 
	 * @param pojo
	 * @return
	 * @throws Exception
	 */
	public static String setException(DSPPojo pojo) throws Exception {
		if (pojo.getStatusCode() != 200 && null != pojo.getFailureMessage()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(DSPConstants.ERROR_CODE.value(),
					pojo.getStatusCode());
			jsonObject.addProperty(DSPConstants.ERROR_DESCRIPTION.value(),
					pojo.getFailureMessage());
			return jsonObject.toString();
		} else {
			return DSPConstants.DB_FAILURE.value();
		}
	}

}
