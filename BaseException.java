package com.dcx.commons.exceptionhandling.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import com.dcx.commons.exceptionhandling.util.Constants;

/**
 *  
 */
public class DCXException extends Exception implements IDCXException {
	private static boolean is14 = true;

	protected Object errorCode;

	protected Object otherInfo;

	protected Throwable baseException = this;

	static {
		try {
			Class.forName(Constants.STACK_TRACE_ELEMENT_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			is14 = false;
		}
	}

	/**
	 * 
	 *  
	 */
	public DCXException() {
		super();
	}

	/**
	 * 
	 * @param errorCode
	 */
	public DCXException(Object errorCode) {
		super();
		this.errorCode = errorCode;
	}

	/**
	 * 
	 * @param message
	 */
	public DCXException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param errorCode
	 * @param message
	 */
	public DCXException(Object errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 
	 * @param cause
	 */
	public DCXException(Throwable cause) {
		this.setBaseException(cause);
	}

	/**
	 * 
	 * @param message
	 * @param baseException
	 */
	public DCXException(String message, Throwable baseException) {
		super(message);
		this.setBaseException(baseException);
	}

	/**
	 * 
	 * @param errorCode
	 * @param baseException
	 */
	public DCXException(Object errorCode, Throwable baseException) {
		super();
		this.errorCode = errorCode;
		this.setBaseException(baseException);
	}

	/**
	 * 
	 * @param errorCode
	 * @param message
	 * @param baseException
	 */
	public DCXException(Object errorCode, String message,
			Throwable baseException) {
		super(message);
		this.errorCode = errorCode;
		this.setBaseException(baseException);
	}

	/**
	 * @return Returns the errorCode.
	 */
	public Object getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            The errorCode to set.
	 */
	public void setErrorCode(Object errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return Returns the otherInfo.
	 */
	public Object getOtherInfo() {
		return otherInfo;
	}

	/**
	 * @param otherInfo
	 *            The otherInfo to set.
	 */
	public void setOtherInfo(Object otherInfo) {
		this.otherInfo = otherInfo;
	}

	/**
	 * @param baseException
	 *            The baseException to set.
	 */
	public void setBaseException(Throwable baseException) {
		if (!is14) {
			if (this.baseException != this)
				throw new IllegalStateException(
						Constants.CAN_NOT_OVERRIDE_CAUSE_MSG);
			if (baseException == this)
				throw new IllegalArgumentException(
						Constants.SELF_CAUSATION_NOT_PERMIT_MSG);
			this.baseException = baseException;
		} else {
			try {
				Method method = this.getClass().getMethod(
						Constants.INITIAL_CAUSE_METHOD_NAME,
						new Class[] { Throwable.class });
				method.invoke(this, new Object[] { baseException });
			} catch (Exception e1) {
				this.baseException = baseException;
			}
		}
		//return this;
	}

	/**
	 * @return Returns the target.
	 */
	public Throwable getBaseException() {
		Throwable result = null;
		if (is14) {
			try {
				Method method = super.getClass().getMethod(
						Constants.GET_CAUSE_METHOD_NAME, null);
				result = (Throwable) method.invoke(this, null);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		if (result != null) {
			return result;
		}

		return (baseException == this ? null : baseException);
	}

	/**
	 *  
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());

		Object eCode = getErrorCode();
		if (eCode != null) {
			sb.append(Constants.ERROR_CODE_OTHER_INFO_SEPERATOR);
			sb.append(Constants.ERROR_CODE_MSG + eCode);
		}

		Object oInfo = getOtherInfo();
		if (oInfo != null) {
			sb.append(Constants.ERROR_CODE_OTHER_INFO_SEPERATOR);
			sb.append(Constants.OTHER_INFORMATION_MSG + oInfo);
		}
		return sb.toString();
	}

	/**
	 *  
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 *  
	 */
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);

		if (baseException != this) {
			s.println(Constants.CAUSED_BY_MSG);
			baseException.printStackTrace(s);
		}
	}

	/**
	 *  
	 */
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);

		if (baseException != this) {
			s.println(Constants.CAUSED_BY_MSG);
			baseException.printStackTrace(s);
		}
	}
}
