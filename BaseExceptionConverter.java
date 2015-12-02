package com.dcx.commons.exceptionhandling.util;

import java.sql.SQLException;

import com.dcx.commons.exceptionhandling.exceptions.persistence.BadSqlGrammarException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.CardinalityViolationsException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.ConnectionException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.CursorErrorException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.DCXPersistenceException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.DataIntegrityViolationException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.FunctionCallException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.NoDataException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.TransactionException;
import com.dcx.commons.exceptionhandling.exceptions.persistence.UncategorizedSQLException;

import java.util.HashSet;
import java.util.Set;

/**
 *This class is for converting the SQL exceptions to the specific DCX exception.
 *As any DataBase throw only SQLException for any error so we have to convert it into proper exception.
 *We identify the cause of exception by the SQl State Class code. 
 */
public class DCXExceptionConvertor {
	private static Set BAD_SQL_CODES = new HashSet();
	private static Set INTEGRITY_VIOLATION_CODES = new HashSet();
	private static Set CONNECTION_EXCEPTION_CODES = new HashSet();
	private static Set NO_DATA_CODES = new HashSet();
	private static Set CURSOR_ERROR_CODES = new HashSet();
	private static Set TRANSACTION_ERROR_CODES = new HashSet();
	private static Set FUNCTION_CALL_CODES = new HashSet();
	private static Set CARDINALITY_VIOLATIONS_CODES = new HashSet();


	static {
		BAD_SQL_CODES.add("07"); //Dynamic SQL Error
		BAD_SQL_CODES.add("42"); //Syntax erre or acees rule violation
		BAD_SQL_CODES.add("65"); // Oracle throws on unknown identifier
		BAD_SQL_CODES.add("S0"); // MySQL uses this - from ODBC error codes?

		INTEGRITY_VIOLATION_CODES.add("22"); // Integrity constraint violation
		INTEGRITY_VIOLATION_CODES.add("23"); // Integrity constraint violation
		INTEGRITY_VIOLATION_CODES.add("27"); // Triggered data change violation
		INTEGRITY_VIOLATION_CODES.add("44"); // With check violation

		NO_DATA_CODES.add("02"); //No Data Exception

		CONNECTION_EXCEPTION_CODES.add("08"); //Connection exception
		CONNECTION_EXCEPTION_CODES.add("2E");

		CURSOR_ERROR_CODES.add("24"); // Invalid cursor state
		CURSOR_ERROR_CODES.add("34"); // Invalid cursor name
		CURSOR_ERROR_CODES.add("36"); // Invalid cursor specifiaction

		TRANSACTION_ERROR_CODES.add("40"); //Transaction rollback
		TRANSACTION_ERROR_CODES.add("25"); //Invalid transaction state
		TRANSACTION_ERROR_CODES.add("2D"); //Invalid transaction termination

		FUNCTION_CALL_CODES.add("38"); //External function exception
		FUNCTION_CALL_CODES.add("39"); //External function call exception
		
		CARDINALITY_VIOLATIONS_CODES.add("21");//Cardinality violations exception


	}

	//This method converts the SQLException to the DCX exception and return the particular DCX Exception.
	public static DCXPersistenceException convert(SQLException sqle) {
		String sqlState = sqle.getSQLState();
		// Some JDBC drivers nest the actual exception from a batched update - need to get the nested one.
		if (sqlState == null) {
			SQLException nestedEx = sqle.getNextException();
			if (nestedEx != null) {
				sqlState = nestedEx.getSQLState();
			}
			
		}
		if (sqlState != null && sqlState.length() >= 2) {
			String classCode = sqlState.substring(0, 2);
			if (BAD_SQL_CODES.contains(classCode)) {
				return new BadSqlGrammarException(sqle);
			}
			if (INTEGRITY_VIOLATION_CODES.contains(classCode)) {
				return new DataIntegrityViolationException(sqle);
			}

			if (NO_DATA_CODES.contains(classCode)) {
				return new NoDataException(sqle);
			}

			if (CONNECTION_EXCEPTION_CODES.contains(classCode)) {
				return new ConnectionException(sqle);
			}

			if (CURSOR_ERROR_CODES.contains(classCode)) {
				return new CursorErrorException(sqle);
			}

			if (TRANSACTION_ERROR_CODES.contains(classCode)) {
				return new TransactionException(sqle);
			}

			if (FUNCTION_CALL_CODES.contains(classCode)) {
				return new FunctionCallException(sqle);
			}
			
			if(CARDINALITY_VIOLATIONS_CODES.contains(classCode)){
				return new CardinalityViolationsException(sqle);
			}
		}

		// We couldn't identify it more precisely.
		return new UncategorizedSQLException(sqle);

	}
}
