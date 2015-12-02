package com.dcx.commons.exceptionhandling.config;

/**
 *  
 */
public class ConfiguredHandler {
	protected String errorCode;

	protected String exceptionClassName;

	protected String handlerClassName;

	public ConfiguredHandler(String errorCode, String exceptionClassName,
			String handlerClassName) {
		this.errorCode = errorCode;
		this.exceptionClassName = exceptionClassName;
		this.handlerClassName = handlerClassName;
	}

	/**
	 * @return Returns the errorCode.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            The errorCode to set.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return Returns the exceptionClassName.
	 */
	public String getExceptionClassName() {
		return exceptionClassName;
	}

	/**
	 * @param exceptionClassName
	 *            The exceptionClassName to set.
	 */
	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	/**
	 * @return Returns the handlerClassName.
	 */
	public String getHandlerClassName() {
		return handlerClassName;
	}

	/**
	 * @param handlerClassName
	 *            The handlerClassName to set.
	 */
	public void setHandlerClassName(String handlerClassName) {
		this.handlerClassName = handlerClassName;
	}

	public String toString() {
		return "(ErrorCode: " + getErrorCode() + "\tExceptionClassName: "
				+ getExceptionClassName() + "\tHandlerClassName: "
				+ getHandlerClassName() + ")";
	}

	/**
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == null || !(obj instanceof ConfiguredHandler)) {
			return result;
		}

		ConfiguredHandler other = (ConfiguredHandler) obj;
		if (other.getExceptionClassName() != null
				&& other.getExceptionClassName().equals(
						this.getExceptionClassName())) {
			if (other.getHandlerClassName() != null
					&& other.getHandlerClassName().equals(
							this.getHandlerClassName())) {
				if (other.getErrorCode() != null
						&& other.getErrorCode().equals(this.getErrorCode())) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * @return int
	 */
	public int hashCode() {
		int result = 0;

		String errorCode = this.getErrorCode();
		String exceptionClassName = this.getExceptionClassName();
		String handlerClassName = this.getHandlerClassName();

		result = (errorCode != null) ? result + errorCode.hashCode() : result;
		result = (exceptionClassName != null) ? result
				+ exceptionClassName.hashCode() : result;
		result = (handlerClassName != null) ? result
				+ handlerClassName.hashCode() : result;

		return 10 * result;
	}
}
