package com.dlh.zambas.dsp.constant;

/**
 * To define constants
 * 
 *
 */
public enum DSPConstants {
	SANDBOX("sandbox"),
	ENV("env"),
	ENABLELOGGER_PROPERTIES("enablelogger.properties"),
	SELECT_QUERY("FROM DSPPojo dsp where dsp.id = :id"),
	SELECT_OUATH_QUERY("FROM OuathDBPojo ouath where ouath.id = :id"),
	MESSAGE("message"),
	HTTPS_PROTOCOL("https.protocols"),
	TLS("TLSv1,TLSv1.1,TLSv1.2"),
	TLSv12("TLSv1.2"),
	PROXY_HOST("StarProxy"),
	PROXY_PORT("StarProxyPort"),
	SERVER_ERROR("Internal Server Error"),
	BEARER("bearer "),
	GMT("GMT"),
	DATE_FORMAT("E MMM dd HH:mm:ss Z yyyy"),
	grant_type("grant_type"),
	CONTENT_TYPE("Content-Type"),
	ACCESS_TOKEN("access_token"),
	EXPIRES_IN("expires_in"),
	REFRESH_TOKEN("refresh_token"),
	AUTHORIZATION("Authorization"),
	ERROR_CODE("errorCode"),
	UNAUTHORIZED("UnAuthorized"),
	DB_FAILURE("DBFailure"),
	INTERNAL_SERVER_ERROR("Internal Server Error"),
	ERROR_DESCRIPTION("message"),
	//DATA_SOURCE("jdbc:/selfservice"), // For TEST
	DATA_SOURCE("jdbc:/sessionpool"), // For INT and PRD
	INITIAL_CONTEXT_FACTORY("weblogic.jndi.WLInitialContextFactory"),
	NAMING_FACTORY("java.naming.factory.initial"),
	PROVIDER_URL("java.naming.provider.url");

	private String value = null;

	DSPConstants(String value) {
		this.value = value;
	}

	private DSPConstants() {
	}

	public String value() {
		return value;
	}
}
