package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		String username = transactionDetails[0];		
		String fromCurrency = transactionDetails[1];
		String toCurrency = transactionDetails[2];
		double amount = Double.parseDouble(transactionDetails[3]);
		
		boolean validTransaction = false;
		boolean nameMatches = false;
		
		
		
		User targetUser = new User();
		Set<String> currencies = FXRates.fxRates.keySet();
		Set<String> validCurrencies = new HashSet<>(currencies);
		validCurrencies.add("usd");
		
		// 0 - name, 1 - from-curr, 2- to-curr, 3- amount in from-curr
		
		if ( !validCurrencies.contains(fromCurrency) || !validCurrencies.contains(toCurrency) ) {
			System.out.println("Invalid currency: transaction skipped");
			validTransaction = false;
		}
		
		if ( fromCurrency.equals(toCurrency) ) {
			System.out.println("FROM-currency equals to TO-currency: transaction skipped");
			validTransaction = false;
		}
		
		for ( User user : userList ) {
			
			if ( username.equalsIgnoreCase(user.getName())) {
				if ( !user.getWallet().containsKey(fromCurrency) ) {
					System.out.println("User does not hold FROM-currency in wallet: transaction skipped");
					validTransaction = false;
				} 
				else {
					if ( user.getWallet().get(fromCurrency) < amount ) {
						System.out.println("User has insufficient FROM-currency in wallet: transaction skipped");
						validTransaction = false;
					}
				}
				nameMatches = true;
				targetUser = user;
				validTransaction = true;
				break;
			} 
		}
		if ( validTransaction ) {
			TreeMap<String, Double> targetWallet = targetUser.getWallet();
			double fromCurrencyRate = targetWallet.get(fromCurrency);
			
			// actual rate is amount of From-curr per To-curr
			double actualRate = Converter.convert(fromCurrency, toCurrency, fromCurrencyRate);
			double amountInToCurr = amount / actualRate;
			
			if ( !targetWallet.containsKey(toCurrency) ) {
				targetWallet.put(toCurrency, 0.0);
			}
			else 
			{
				targetWallet.replace(fromCurrency, targetWallet.get(fromCurrency) - amount );
				targetWallet.replace(toCurrency, targetWallet.get(toCurrency) + amountInToCurr);
			}
			
			updateUsersFile();
			System.out.println("Transaction completed: " + transaction);
		}
		else if ( !nameMatches ) {
			System.out.println("User does not exist in users.json: transaction skipped");
		}
		
	}

	public void updateUsersFile() {
		FileOutputStream fileOut;
		File usersFile = new File(Runner.getFilePath("users-test.json"));
		
		try {
			fileOut = new FileOutputStream(usersFile);
			String jsonString = mapper.writeValueAsString(userList);
			fileOut.write(jsonString.getBytes());
			
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

	private void readUsersFile() throws StreamReadException, DatabindException, IOException {

		TypeReference<List<User>> userListType = new TypeReference<List<User>>() {};
		File usersFile = new File(Runner.getFilePath("users.json"));
		this.userList = mapper.readValue(usersFile, userListType);
	}

	private void getListOfTransactionsFromFile() throws FileNotFoundException, IOException {
		FileInputStream fileIn = new FileInputStream(Runner.getFilePath("transactions.txt"));
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
