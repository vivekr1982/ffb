package com.dcx.commons.exceptionhandling.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dcx.commons.logging.LogFactory;
import com.dcx.commons.logging.Logger;

/**
 *  
 */
public class DefaultConfigurationImpl extends Configuration {
	private static final transient Logger logger = LogFactory
			.getLogger(DefaultConfigurationImpl.class);

	private String fileName;

	protected String defaultHandlerClassName;

	protected Map exceptionToHandlerMap;

	private boolean isInitialized = false;

	protected class HandlerMapKey {
		private String errorCode;

		private String exceptionClassName;

		public HandlerMapKey(String errorCode, String exceptionClassName) {
			this.errorCode = errorCode;
			this.exceptionClassName = exceptionClassName;
		}

		public boolean equals(Object obj) {
			boolean result = false;
			if (obj == null || !(obj instanceof HandlerMapKey)) {
				return result;
			}

			HandlerMapKey other = (HandlerMapKey) obj;

			if (other.errorCode != null
					&& other.errorCode.equals(this.errorCode)) {
				if (other.exceptionClassName != null
						&& other.exceptionClassName
								.equals(this.exceptionClassName)) {
					result = true;
				}
			}
			return result;
		}

		public int hashCode() {
			int result = 0;

			result = (this.errorCode != null) ? result
					+ this.errorCode.hashCode() : result;
			result = (this.exceptionClassName != null) ? result
					+ this.exceptionClassName.hashCode() : result;
			return 10 * result;
		}
	}

	public DefaultConfigurationImpl(String fileName) {
		this.fileName = fileName;
	}

	private Map getConfiguredHandlerMap(Node rootElement) {
		Map configuredHandlerMap = new HashMap();

		if (rootElement != null) {
			NodeList exceptionList = rootElement.getChildNodes();

			if (exceptionList != null && exceptionList.getLength() > 0) {
				for (int i = 0; i < exceptionList.getLength(); i++) {
					Node currentExceptionNode = exceptionList.item(i);
					String exceptionClassName = null;
					String errorCode = null;
					String exceptionHandlerClassName = null;
					if (currentExceptionNode != null
							&& currentExceptionNode.getNodeName() != null
							&& currentExceptionNode.getNodeName().equals(
									"exception")) {
						NamedNodeMap attributeMap = currentExceptionNode
								.getAttributes();
						if (attributeMap != null
								&& attributeMap.getLength() > 0) {
							Node exceptionClassAttribute = attributeMap
									.getNamedItem("class");
							if (exceptionClassAttribute != null) {
								exceptionClassName = exceptionClassAttribute
										.getNodeValue();
							}
							Node errorCodeAttribute = attributeMap
									.getNamedItem("errorcode");
							if (errorCodeAttribute != null) {
								errorCode = errorCodeAttribute.getNodeValue();
							}
						}
						NodeList exceptionHandlerNodeList = currentExceptionNode
								.getChildNodes();
						if (exceptionHandlerNodeList != null
								&& exceptionHandlerNodeList.getLength() > 0) {
							for (int k = 0; k < exceptionHandlerNodeList
									.getLength(); k++) {
								Node currentExceptionHandlerNode = exceptionHandlerNodeList
										.item(k);
								if (currentExceptionHandlerNode != null
										&& currentExceptionHandlerNode
												.getNodeName() != null
										&& currentExceptionHandlerNode
												.getNodeName()
												.equals("handler")) {
									NamedNodeMap handlerAttributeMap = currentExceptionHandlerNode
											.getAttributes();
									if (handlerAttributeMap != null
											&& handlerAttributeMap.getLength() > 0) {
										Node handlerNode = handlerAttributeMap
												.getNamedItem("class");
										if (handlerNode != null) {
											exceptionHandlerClassName = handlerNode
													.getNodeValue();
										}
									}
								}
							}
						}
					}
					if (exceptionHandlerClassName != null
							&& exceptionClassName != null) {
						ConfiguredHandler currentConfiguredHandler = new ConfiguredHandler(
								errorCode, exceptionClassName,
								exceptionHandlerClassName);
						HandlerMapKey mapKey = new HandlerMapKey(errorCode,
								exceptionClassName);
						configuredHandlerMap.put(mapKey,
								currentConfiguredHandler);
						logger.debug("ConfiguredHandler "
								+ currentConfiguredHandler + " is created.");

					}
				}
			}
		}
		return configuredHandlerMap;
	}

	private void parseXMLAndInitMap(Document xmlDom) {
		NodeList configElementList = xmlDom.getElementsByTagName("exceptions");
		if (configElementList != null && configElementList.getLength() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("elements present in " + this.fileName);
			}
			for (int i = 0; i < configElementList.getLength(); i++) {
				Node rootNode = configElementList.item(i);
				if (rootNode != null
						&& rootNode.getNodeName().equals("exceptions")) {
					NamedNodeMap rootAttributeMap = rootNode.getAttributes();
					if (rootAttributeMap != null
							&& rootAttributeMap.getLength() > 0) {
						Node defaultHandlerClassName = rootAttributeMap
								.getNamedItem("default");
						if (defaultHandlerClassName != null) {
							this.defaultHandlerClassName = defaultHandlerClassName
									.getNodeValue();
						}
					}
					exceptionToHandlerMap = getConfiguredHandlerMap(rootNode);
				}
			}
		}

	}

	public void init() {
		try {
			File configFile = new File(fileName);
			FileInputStream configFileStream = new FileInputStream(configFile);
			DocumentBuilderFactory configDocFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder configDocBuilder = configDocFactory
					.newDocumentBuilder();
			Document configXmlDom = configDocBuilder.parse(configFileStream);
			parseXMLAndInitMap(configXmlDom);
		} catch (FileNotFoundException e) {
			logger.error("File does not exist (" + fileName + ")", e);
		} catch (FactoryConfigurationError e) {
			logger.error("Exception occured while configuring the factory", e);
		} catch (ParserConfigurationException e) {
			logger.error("Exception occured while parsing the file.", e);
		} catch (SAXException e) {
			logger.error("Exception occured while parsing the file.", e);
		} catch (IOException e) {
			logger.error("Exception occured while reading the file.", e);
		}
		if (exceptionToHandlerMap == null) {
			exceptionToHandlerMap = new HashMap();
		}
		isInitialized = true;
	}

	/**
	 * @return Returns the defaultHandlerClassName.
	 */
	public String getDefaultHandlerClassName() {
		if (!isInitialized) {
			init();
		}
		return defaultHandlerClassName;
	}

	/**
	 * 
	 * @see com.dcx.commons.exceptionhandling.config.Configuration#getConfiguredHandler(java.lang.String,
	 *      java.lang.String)
	 */
	public ConfiguredHandler getConfiguredHandler(String errorCode,
			String exceptionClassName) {
		if (!isInitialized) {
			init();
		}
		ConfiguredHandler configuredHandler = (ConfiguredHandler) exceptionToHandlerMap
				.get(new HandlerMapKey(errorCode, exceptionClassName));
		return configuredHandler;
	}

	/**
	 * 
	 * @see com.dcx.commons.exceptionhandling.config.Configuration#isHandlerExistsFor(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isHandlerExistsFor(String errorCode,
			String exceptionClassName) {
		if (!isInitialized) {
			init();
		}
		return exceptionToHandlerMap.containsKey(new HandlerMapKey(errorCode,
				exceptionClassName));
	}

	/**
	 * 
	 * @see com.dcx.commons.exceptionhandling.config.Configuration#getAllConfiguredHandlers()
	 */
	public Set getAllConfiguredHandlers() {
		if (!isInitialized) {
			init();
		}
		Set result = new HashSet();
		Iterator iterator = exceptionToHandlerMap.keySet().iterator();
		while (iterator.hasNext()) {
			result.add(exceptionToHandlerMap.get(iterator.next()));
		}
		return result;
	}
}
