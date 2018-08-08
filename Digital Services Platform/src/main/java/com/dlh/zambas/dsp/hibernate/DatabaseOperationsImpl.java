package com.dlh.zambas.dsp.hibernate;

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;

import com.dlh.zambas.backend.operation.ITokenOperations;
import com.dlh.zambas.backend.operation.TokenOperationsImpl;
import com.dlh.zambas.dsp.constant.DSPConstants;
import com.dlh.zambas.dsp.pojo.DSPPojo;
import com.dlh.zambas.dsp.pojo.OauthDBPojo;
import com.dlh.zambas.mdw.logger.AppLog;
import com.dlh.zambas.utils.Utility;

/**
 * 
 * Class gets called for performing database operations
 *
 */
public class DatabaseOperationsImpl implements IDatabaseOperations {

	private Query query = null;
	private AppLog appLog = AppLog.getAppLog();
	private ITokenOperations tokenOperations = null;
	private DSPPojo pojo = null;
	private Session session = null;

	/**
	 * returns values from starhub_dsp_token table based on crNumber
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public DSPPojo returnDBValues(int crNumber) {
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			pojo = (DSPPojo) session.get(DSPPojo.class, new Integer(crNumber));
			if (null != pojo) {
				appLog.info("Token : " + pojo.getToken());
				appLog.info("Expires In : " + pojo.getExpirationTime());
				appLog.info("Last modified time : "
						+ pojo.getLastModifiedTime());
			}

		} catch (Exception e) {
			appLog.error("No record found in DB ", e);
		} finally {
			closeSession(session);
		}
		return pojo;
	}

	/**
	 * saves values returned from ouath2 server in starhub_dsp_token table
	 * 
	 * @param session
	 * @param starDSPURI
	 * @param crNumber
	 * @throws Exception
	 */
	public void insertIntoDB(int crNumber) throws Exception {

		try {
			tokenOperations = new TokenOperationsImpl();
			pojo = tokenOperations.fetchValidAccessToken(crNumber);
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			// Save the values in database
			session.save(pojo);

			// Commit the transaction
			session.getTransaction().commit();

		} catch (Exception e) {
			appLog.error("insertion failed : ", e);
			throw new Exception(Utility.setException(pojo), e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * updates values returned from ouath2 server in starhub_dsp_token table
	 * 
	 * @param session
	 * @param pojo
	 * @return
	 * @throws Exception
	 */

	public synchronized String updateEntries(DSPPojo pojo) throws Exception {

		try {
			// check if token has been updated by some other thread/user
			if (isTokenUpdated(pojo.getId())) {
				appLog.info("token updated already");
				// return token
				return returnDBValues(pojo.getId()).getToken();
			} else {
				// update table with latest ouath2 token
				appLog.info("Update called");
				tokenOperations = new TokenOperationsImpl();
				DSPPojo updatePojo = tokenOperations.fetchValidAccessToken(pojo.getId());
				session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();

				query = session.createQuery(DSPConstants.SELECT_QUERY.value());
				query.setParameter("id", pojo.getId());
				pojo = new DSPPojo();
				pojo = (DSPPojo) (query).list().get(0);
				pojo.setToken(updatePojo.getToken());
				pojo.setLastModifiedTime(Utility.timeInGMTTimeZone(
						new Date().toString()).toString());
				pojo.setExpirationTime(updatePojo.getExpirationTime());
				session.update(pojo);

				// Commit the transaction
				session.getTransaction().commit();

				return pojo.getToken() != null ? pojo.getToken() : null;
			}
		} catch (Exception e) {
			appLog.error("Exception occured while updating DB record ", e);
			throw new Exception(Utility.setException(pojo), e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * check whether token has been updated in last 60 mins (pojo
	 * .getExpirationTime())
	 * 
	 * @param crNumber
	 * @return
	 * @throws Exception
	 */
	private boolean isTokenUpdated(int crNumber) throws Exception {
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			// refresh cache
			session.refresh(pojo);
			DSPPojo dspPojo = returnDBValues(crNumber);
			if (Utility.timeInGMTTimeZone(dspPojo.getLastModifiedTime())
					.before(Utility.timeInGMTTimeZone(new Date().toString()))
					&& (Utility.timeInGMTTimeZone(new Date().toString())
							.getTime()
							- Utility.timeInGMTTimeZone(
									dspPojo.getLastModifiedTime()).getTime() < pojo
							.getExpirationTime() * 1000)) {
				return true;
			}
			return false;
		} finally {
			closeSession(session);
		}
	}

	/**
	 * fetch client_id and client_secret These get used to fetch ouath token
	 * details
	 * 
	 * @param crNumber
	 * @return
	 * @throws Exception
	 */
	public OauthDBPojo fetchOauthDetails(int crNumber) throws Exception {
		OauthDBPojo ouathDBPojo = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			ouathDBPojo = (OauthDBPojo) session.get(OauthDBPojo.class,
					new Integer(crNumber));
			if (null != ouathDBPojo) {
				appLog.info("Client_ID : " + ouathDBPojo.getClientId());
				appLog.info("Client_Secret : " + ouathDBPojo.getClientSecret());
			}
		} catch (Exception e) {
			appLog.error(
					"Exception occured while fetching client_id and client_secret",
					e);
			throw new Exception(DSPConstants.DB_FAILURE.value(), e);
		} finally {
			closeSession(session);
		}
		return ouathDBPojo;
	}

	/**
	 * close hibernate session
	 * 
	 * @param session
	 */
	private void closeSession(Session session) {
		if (null != session && session.isOpen()) {
			appLog.info("closing hibernate session");
			session.close();
		}
	}


}
