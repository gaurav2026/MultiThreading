package com.dlh.zambas.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.dlh.zambas.dsp.constant.DSPConstants;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

/**
 * 
 * class gets called for enforcement of TLS1.2 as well as for routing call via
 * proxy server Proxy values are set as System properties
 *
 */
public class ConnectionFactory implements HttpURLConnectionFactory {

	static {
		System.setProperty(DSPConstants.HTTPS_PROTOCOL.value(),
				DSPConstants.TLS.value());
	}

	Proxy proxy;

	/**
	 * called for routing call via proxy
	 * 
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public ConnectionFactory(String url) throws MalformedURLException,
			IOException {
		getHttpURLConnection(new URL(url));
	}

	/**
	 * proxy ip and port are set as system properties
	 */
	private void initializeProxy() {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				System.getProperty(DSPConstants.PROXY_HOST.value()),
				Integer.parseInt(System.getProperty(DSPConstants.PROXY_PORT
						.value()))));
		
	}

	/**
	 * get http URL connection
	 */
	public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
		initializeProxy();
		return (HttpURLConnection) url.openConnection(proxy);
	}

	/**
	 * method gets called for enforcement of TLS1.2
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized SSLContext getSslContext() throws Exception {SSLContext sslContext = null;
	try {
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] certs,
				String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs,
				String authType) {
		}
	} };

	// Ignore differences between given hostname and certificate hostname
	HostnameVerifier hv = new HostnameVerifier() {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	// Install the all-trusting trust manager
		sslContext = SSLContext.getInstance(DSPConstants.TLSv12.value());
		sslContext.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
				.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	} catch (Exception e) {
		throw new Exception("Exception occured while routing call though proxy " , e);
	}
	return sslContext;
}
 
}