package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class TransactionsProcessor {

	List<String> transactionsListToExecute;
	List<User> userList;
	ObjectMapper mapper = new ObjectMapper();
	Logger transactionsLogger = LogManager.getLogger( TransactionsProcessor.class );

	public TransactionsProcessor() {
		transactionsListToExecute = new ArrayList<>();
		try {
			getListOfTransactionsFromFile();
			readUsersFile();
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void executeTransaction(String transaction) {
		String[] transactionDetails = transaction.split(" ");
				
		boolean validTransaction = false;
		boolean nameMatches = false;
		List<String> validCurrencies = new ArrayList<>();
		validCurrencies.add("usd");
		for ( Currency currency : FXRatesList.currencyRateList ) {
			validCurrencies.add(currency.getCode());
		}
		// 0 - name, 1 - from-curr, 2- to-curr, 3- amount in from-curr
		
		if ( !validCurrencies.contains(transactionDetails[1]) || !validCurrencies.contains(transactionDetails[2]) ) {
			System.out.println("Invalid currency: transaction skipped");
			validTransaction = false;
		}
		
		if ( transactionDetails[1].equals(transactionDetails[2]) ) {
			System.out.println("FROM-currency equals to TO-currency: transaction skipped");
			validTransaction = false;
		}
		
		for ( User user : userList ) {
			
			if ( transactionDetails[0].equalsIgnoreCase(user.getName())) {
				if ( !user.getWallet().containsKey(transactionDetails[1]) ) {
					System.out.println("User does not hold FROM-currency in wallet: transaction skipped");
					validTransaction = false;
				} 
				else {
					if ( user.getWallet().get(transactionDetails[1]) < Double.parseDouble(transactionDetails[3])) {
						System.out.println("User has insufficient FROM-currency in wallet: transaction skipped");
						validTransaction = false;
					}
				}
				nameMatches = true;
				validTransaction = true;
				break;
			} 
		}
		if ( validTransaction ) {
			
			
			updateUsersFile();
			System.out.println("Transaction completed: " + transaction);
		}
		else if ( !nameMatches ) {
			System.out.println("User does not exist in users.json: transaction skipped");
		}
		
	}

	public void updateUsersFile() {
		
		
	}

	private void readUsersFile() throws StreamReadException, DatabindException, IOException {

		TypeReference<List<User>> userListType = new TypeReference<List<User>>() {};
		File usersFile = new File(Runner.getFilePath("users-test.json"));
		this.userList = mapper.readValue(usersFile, userListType);
	}

	private void getListOfTransactionsFromFile() throws FileNotFoundException, IOException {
		FileInputStream fileIn = new FileInputStream(Runner.getFilePath("transactions-test.txt"));
		byte[] transactions = fileIn.readAllBytes();
		StringBuilder transactionString = new StringBuilder();

		for (byte b : transactions) {
			if (b == 10) {
				transactionsListToExecute.add(transactionString.toString());
				transactionString.setLength(0);
			} else {
				transactionString.append((char) b);
			}
		}
		fileIn.close();
	}

	public List<String> getListOfTransactionsToExecute() {
		return this.transactionsListToExecute;
	}

}
