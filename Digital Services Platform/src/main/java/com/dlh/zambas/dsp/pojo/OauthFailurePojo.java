package com.dlh.zambas.dsp.pojo;

/**
 * used to map failed scenario while fetching ouath2 token
 * @author singhg
 *
 */
public class OauthFailurePojo {
	private String status = null;
	private String code = null;
	private String message = null;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
