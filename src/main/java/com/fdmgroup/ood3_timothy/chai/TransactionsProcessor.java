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

/**
 * The TransactionsProcessor class holds all transaction-related methods as well
 * as the list of users, so that user information is dynamically amended with
 * each valid transaction
 * 
 */
class TransactionsProcessor {

	// Attributes
	List<String> transactionsListToExecute;
	List<User> userList;
	ObjectMapper mapper = new ObjectMapper();
	Logger transactionsLogger = LogManager.getLogger(TransactionsProcessor.class);

	/**
	 * The default constructor for TransactionsProcessor initializes the userList
	 * and transactionsListToExecute attributes to be used by all other methods.
	 * 
	 */
	public TransactionsProcessor() {
		transactionsListToExecute = new ArrayList<>();
		try {
			getListOfTransactionsFromFile();
			readUsersFile();
		} catch (FileNotFoundException fe) {
			transactionsLogger.warn(fe.getMessage());

		} catch (IOException e) {
			transactionsLogger.warn(e.getMessage());
		}
	}

	/**
	 * This is the main method used to execute all transactions, and holds all the
	 * logic for checking if a passed transaction is valid The method checks all the
	 * requirements: - whether from-currency and to-currency are equal - whether
	 * from-currency or to-currency are valid currencies - whether the user
	 * specified is valid - whether a valid user is holding from-currency - whether
	 * a valid user has sufficient from-currency
	 * 
	 * If a transaction is invalid, a relevant message is printed onto console and
	 * appended to src/main/logs/transactions-log.log If a transaction is valid, the
	 * user's wallet details are amended to reflect the change and likewise a
	 * message is appended to src/main/logs/transactions-log.log
	 * 
	 * @param transaction an input string formatted as 'name from-currency
	 *                    to-currency amount-in-from-currency'
	 */
	public void executeTransaction(String transaction) {

		// The input transaction string is split by the delimiter and assigned into
		// relevant named variables
		String[] transactionDetails = transaction.split(" ");
		String username = transactionDetails[0];
		String fromCurrency = transactionDetails[1];
		String toCurrency = transactionDetails[2];
		double amount = Double.parseDouble(transactionDetails[3]);

		// Boolean flags used for transaction validity checking
		boolean validTransaction = false;
		boolean nameMatches = false;

		// Instantiation of new User object for assignment later
		User targetUser = new User();

		// Calls the currencyCode-rate TreeMap of FXRates to get a list of valid
		// currencies, and appending 'usd' to avoid flagging out usd transactions
		Set<String> currencies = FXRates.fxRates.keySet();
		Set<String> validCurrencies = new HashSet<>(currencies);
		validCurrencies.add("usd");

		// Checks if any of the currencies of the transaction are valid by comparing
		// against list of valid currencies
		if (!validCurrencies.contains(fromCurrency) || !validCurrencies.contains(toCurrency)) {
			transactionsLogger.info("Invalid currency: transaction skipped : " + transaction);
			validTransaction = false;
		}

		// Checks if both currencies are equal
		if (fromCurrency.equals(toCurrency)) {
			transactionsLogger.info("FROM-currency equals to TO-currency: transaction skipped : " + transaction);
			validTransaction = false;
		}

		// Checks transaction name against each user of userList
		// userList is obtained from readUsersFile
		for (User user : userList) {

			// Checks if transaction name is valid
			if (username.equalsIgnoreCase(user.getName())) {
				nameMatches = true;
				targetUser = user;
				validTransaction = true;

				// If transaction name is valid, check if said user holds from-Currency
				if (!user.getWallet().containsKey(fromCurrency)) {
					transactionsLogger
							.info("User does not hold FROM-currency in wallet: transaction skipped : " + transaction);
					validTransaction = false;
				} else {
					// If user holds from-Currency, check if balance is above transaction amount
					if (user.getWallet().get(fromCurrency) < amount) {
						transactionsLogger.info(
								"User has insufficient FROM-currency in wallet: transaction skipped : " + transaction);
						validTransaction = false;
					}
				}
				// If a user is valid, then do not check other users
				break;
			}
		}
		// If transaction has passed all checks, it is valid and we can amend wallet
		// details
		if (validTransaction) {

			// Get target wallet of user
			TreeMap<String, Double> targetWallet = targetUser.getWallet();

			// Calculate amount to add to 'to-Currency'
			double amountInToCurr = Converter.convert(fromCurrency, toCurrency, amount);

			// If wallet does not have to-Currency, create a key-value pair
			if (!targetWallet.containsKey(toCurrency)) {
				targetWallet.put(toCurrency, 0.0);
			}
			// Change balances of fromCurrency and toCurrency to correct values

			targetWallet.replace(fromCurrency, targetWallet.get(fromCurrency) - amount);
			targetWallet.replace(toCurrency, targetWallet.get(toCurrency) + amountInToCurr);

			// If a user has zero balance after the transaction, remove currency from wallet
			for (String holdingCurrency : targetWallet.keySet()) {
				if (targetWallet.get(holdingCurrency) == 0) {
					targetWallet.remove(holdingCurrency);
				}
			}

			// Update users.json file once done
			updateUsersFile();
			transactionsLogger.trace("Transaction completed: " + transaction);
		}
		// If user is invalid, display and log relevant message
		else if (!nameMatches) {
			transactionsLogger.info("User does not exist in users.json: transaction skipped : " + transaction);
		}

	}

	/**
	 * This method takes the users.json file and updates the information based on
	 * the current values of each User object in the userList
	 * 
	 */
	public void updateUsersFile() {
		// Instantiate File class with src/main/resources/users.json
		File usersFile = new File(Runner.getFilePath("users.json"));

		// Updates user information to json format then write to users.json file
		try (FileOutputStream fileOut = new FileOutputStream(usersFile)) {
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

	/**
	 * Reads users.json file and sets it as userList
	 * 
	 * @throws StreamReadException Thrown during parsing issues
	 * @throws DatabindException   Thrown when Jackson is unable to deserialize to
	 *                             specified type reference
	 * @throws IOException         Thrown when other IO-related problems occur
	 */
	private void readUsersFile() throws StreamReadException, DatabindException, IOException {

		TypeReference<List<User>> userListType = new TypeReference<List<User>>() {
		};
		File usersFile = new File(Runner.getFilePath("users.json"));
		this.userList = mapper.readValue(usersFile, userListType);
	}

	/**
	 * Reads the transactions.txt file and returns list of transactions to be
	 * executed
	 * 
	 * @throws FileNotFoundException Thrown when src/main/resources/transactions.txt
	 *                               is not found
	 * @throws IOException           Thrown in other IO-related problems
	 */
	private void getListOfTransactionsFromFile() throws FileNotFoundException, IOException {

		// Creates FileInputStream instance with transactions.txt, and reads into byte
		// array
		FileInputStream fileIn = new FileInputStream(Runner.getFilePath("transactions.txt"));
		byte[] transactions = fileIn.readAllBytes();

		// Instantiate a StringBuilder class to iteratively append characters that
		// belong in the same transaction
		StringBuilder transactionString = new StringBuilder();

		for (byte b : transactions) {
			// If the character is a newline character, transaction has ended, and current
			// string should be added to list
			// Once current string is added to list, remove all characters and add next
			// character that is not a new line
			if (b == 10) {
				transactionsListToExecute.add(transactionString.toString());
				transactionString.setLength(0);
			} else {
				transactionString.append((char) b);
			}
		}
		// Close system resources
		fileIn.close();
	}

	public List<String> getListOfTransactionsToExecute() {
		return this.transactionsListToExecute;
	}

}
