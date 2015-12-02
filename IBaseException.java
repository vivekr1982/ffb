package com.dcx.commons.exceptionhandling.exceptions;

import java.io.Serializable;

/**
 * This is the root interface for all DCX exceptions.
 * 
 * All of the exceptions which are going to be handle by DCX exception handling
 * framework should implement this interface.
 */
public interface IDCXException extends Serializable {
	/**
	 * Gets the error code of exception.
	 * 
	 * @return object - error code of the exception.
	 */
	public Object getErrorCode();

	/**
	 * Sets the error code of exception.
	 * 
	 * @param errorCode -
	 *            error code of the exception.
	 */
	public void setErrorCode(Object errorCode);

	/**
	 * Gets the other information provided by the corresponding exception.
	 * 
	 * @return Object - other information of exception cause.
	 */
	public Object getOtherInfo();

	/**
	 * Sets the other information for the corresponding exception.
	 * 
	 * Other information may be anything which describes the cause exception
	 * breifly.
	 * 
	 * @param otherInfo -
	 *            extra information to find most specific cause.
	 */
	public void setOtherInfo(Object otherInfo);

	/**
	 * Sets the base exception which causes for this exception.
	 * 
	 * @param baseException
	 */
	public void setBaseException(Throwable baseException);

	/**
	 * Gets the base exception which causes for this exception.
	 * 
	 * @return
	 */
	public Throwable getBaseException();

	/**
	 * This method is just like {@link Object#toString()}. The reason to
	 * specify this here is to tell the user, it is better to overrice this
	 * method.
	 * 
	 * This is also the first line of {@link Exception#printStactTrace()}
	 * 
	 * @return string - informatin of the exception.
	 */
	public String toString();
}
