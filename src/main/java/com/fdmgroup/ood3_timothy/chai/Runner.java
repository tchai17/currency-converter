package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

/** This is the main runner class used to execute all other classes to achieve the transaction operations
 *  
 * 
 */
public class Runner {

	public static Logger mainLogger = LogManager.getLogger(Runner.class);

	public static void main(String[] args) {

		
		// Instantiate TransactionsProcessor class
		TransactionsProcessor transactionsProcessor = new TransactionsProcessor();
		try {
			// Initialize FXRates TreeMap for reference by other classes
			FXRates.initializeFXratesToMapFromFile();

			// Calls getListofTransactionsToExecute to return a list of transactions read from transactions.txt
			// Each transaction is then executed iteratively
			for (String transaction : transactionsProcessor.getListOfTransactionsToExecute()) {
				transactionsProcessor.executeTransaction(transaction);
			}

		} catch (StreamReadException sre) {
			mainLogger.warn("stream read exception" + sre.getMessage());
			

		} catch (DatabindException dbe) {
			mainLogger.warn("databind exception" + dbe.getMessage());
			

		} catch (IOException io) {
			mainLogger.warn("io exception" + io.getMessage());
		}

	}

	
	/** This method obtains the absolute path based on the current working directory of the project
	 * 
	 * @param fileName String of file name to be read or written to
	 * @return returnFilePath String of full absolute file path of fileName
	 */
	public static String getFilePath(String fileName) {
		
		// Gets absolute path of a temporary empty file created in this project
		String currentDirPath = new File("").getAbsolutePath();
		
		// Appends src\main\resources and filename to above absolute path
		String resourceFolderPath = currentDirPath + "\\src\\main\\resources\\";
		String returnFilepath = resourceFolderPath + fileName;
		return returnFilepath;
	}

}
