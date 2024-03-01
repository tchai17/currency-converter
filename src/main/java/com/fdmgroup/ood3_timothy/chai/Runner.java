package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

public class Runner {

	public Logger mainLogger = LogManager.getLogger(Runner.class);
	
	public static void main(String[] args) {
		String filePath = getFilePath("fx_rates.json");
		FXRatesList fxRates = new FXRatesList();
		TransactionsProcessor transactionsProcessor = new TransactionsProcessor();
		try {
			fxRates.initializeFXratesToListFromFile(filePath);
			transactionsProcessor.updateUsersFile();
		} 
		catch (StreamReadException sre) {
			System.out.println("stream read exception");
			System.out.println(sre.getMessage());
			
		} catch (DatabindException dbe) {
			System.out.println("databind exception");
			System.out.println(dbe.getMessage());
			
		} catch (IOException io) {
			System.out.println(io.getMessage());
		} 
		

	}
	
	public static String getFilePath(String fileName) {
		String currentDirPath = new File("").getAbsolutePath();
		String resourceFolderPath = currentDirPath + "\\src\\main\\resources\\";
		
		String returnFilepath = resourceFolderPath + fileName;
		return returnFilepath;
	}

}