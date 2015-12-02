package com.dcx.commons.exceptionhandling.config;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Set;

import com.dcx.commons.logging.LogFactory;
import com.dcx.commons.logging.Logger;

/**
 *  
 */
public abstract class Configuration {
	private static final transient Logger logger = LogFactory
			.getLogger(DefaultConfigurationImpl.class);

	private static final String defaultConfigurationClassName = "com.dcx.commons.exceptionhandling.config.DefaultConfigurationImpl";

	private static Class configurationClass = null;

	private static HashMap configurationCache = new HashMap();

	public static Configuration getInstance(String fileName) {
		Configuration returnValue = (Configuration) configurationCache
				.get(fileName);
		if (returnValue != null) {
			return returnValue;
		}

		if (configurationClass == null) {
			logger.debug("Using " + Configuration.defaultConfigurationClassName
					+ " as configuration class.");
			ClassLoader contextClassLoader = (ClassLoader) AccessController
					.doPrivileged(new PrivilegedAction() {
						public Object run() {
							ClassLoader classLoader = null;

							try {
								classLoader = Thread.currentThread()
										.getContextClassLoader();
							} catch (SecurityException e) {
								classLoader = Configuration.class
										.getClassLoader();
							}
							return classLoader;
						}
					});
			if (contextClassLoader != null) {
				try {
					configurationClass = contextClassLoader
							.loadClass(defaultConfigurationClassName);
				} catch (ClassNotFoundException e) {
					logger.error("Can not load default configuration class ("
							+ defaultConfigurationClassName + ")", e);
				}

				if (configurationClass == null) {
					return returnValue;
				}

				try {
					Constructor constructor = configurationClass
							.getConstructor(new Class[] { String.class });
					returnValue = (Configuration) constructor
							.newInstance(new Object[] { fileName });
				} catch (Exception e) {
					logger.error("Can not create instance of "
							+ configurationClass.getName()
							+ " with arguments(String)", e);
				}
			}
		}
		if (returnValue != null) {
			synchronized (configurationCache) {
				configurationCache.put(fileName, returnValue);
			}
		}
		return returnValue;
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getDefaultHandlerClassName();

	/**
	 * 
	 * @param errorCode
	 * @param exceptionClassName
	 * @return
	 */
	public abstract ConfiguredHandler getConfiguredHandler(String errorCode,
			String exceptionClassName);

	/**
	 * 
	 * @param erroCode
	 * @param exceptionClassName
	 * @return
	 */
	public abstract boolean isHandlerExistsFor(String erroCode,
			String exceptionClassName);

	/**
	 * 
	 * @return
	 */
	public abstract Set getAllConfiguredHandlers();

	/**
	 * @return Returns the defaultConfigurationImpl.
	 */
	public static String getConfigurationClassName() {
		return (Configuration.configurationClass != null) ? Configuration.configurationClass
				.getName()
				: null;
	}

	/**
	 * @param defaultConfigurationImpl
	 *            The defaultConfigurationImpl to set.
	 */
	public static void setConfigurationClassName(String configurationClassName) {
		try {
			Configuration.configurationClass = Class
					.forName(configurationClassName);
		} catch (ClassNotFoundException cnfe) {
			logger.warn("Can not set " + configurationClassName
					+ " as configuration class", cnfe);
			logger.warn("Using " + Configuration.defaultConfigurationClassName
					+ " as configuration class.");
			Configuration.configurationClass = null;
		}
	}

	/**
	 * @return Returns the defaultConfigurationImplClass.
	 */
	public static Class getConfigurationClass() {
		return configurationClass;
	}

	/**
	 * @param defaultConfigurationImplClass
	 *            The defaultConfigurationImplClass to set.
	 */
	public static void setConfigurationClass(Class configurationClass) {
		Configuration.configurationClass = configurationClass;
	}
}
