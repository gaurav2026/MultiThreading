package com.dlh.zambas.dsp.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.dlh.zambas.mdw.logger.AppLog;

/**
 * Util Class to create session factory
 * 
 * @author singhg
 *
 */
public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {

		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();

		} catch (Exception ex) {

			// Make sure you log the exception, as it might be swallowed
			AppLog.getAppLog().error("Initial SessionFactory creation failed.",
					ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}
