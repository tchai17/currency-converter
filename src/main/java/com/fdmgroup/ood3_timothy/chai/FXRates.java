package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;


/** This class reads the FX rates from src/main/resources/fx_rates.json and stores them in a 
 * static TreeMap for reference by other classes
 * It contains one main method initializeFXratesToMapFromFile() which reads the json file and initializes the TreeMap.
 * 
 */
public class FXRates {

	static Map<String, Double> fxRates = new TreeMap<>();

	public static void addToFXRates(String code, Double rate) {
		fxRates.put(code, rate);
	}

	
	/** This method reads the rates from src/main/resources/fx_rates.json
	 * Since the json file has multiple nested objects, a nested Map type was used to accept higher level currency code as a super-key, 
	 * with a nested inner Map containing all the relevant attributes required for the currency.
	 * 
	 * @throws StreamReadException Thrown during parsing issues of the json file (unable to read)
	 * @throws DatabindException Thrown when the Jackson library is unable to deserialize the json object
	 * @throws IOException Thrown for other IO-related problems
	 */
	public static void initializeFXratesToMapFromFile() throws StreamReadException, DatabindException, IOException {

		// Create instance of File using fx_rates.json file to be passed to ObjectMapper
		File fxFile = new File(Runner.getFilePath("fx_rates.json"));
		
		// Create instance of ObjectMapper
		ObjectMapper currencyMapper = new ObjectMapper();

		
		// Reads the json file, and converts into a nested Map, saving the main currency code as a String super-key, then saving all 
		// other attributes in sub-map. In this case, only the code and rate is read from the file, all other properties are ignored
		Map<String, Map<String, Double>> nestedCurrencyMap = currencyMapper.readValue(fxFile, Map.class);
		
		// This loop iterates through the super-map to obtain the relevant currency code and rate of each sub-map
		for (Map.Entry<String, Map<String, Double>> currencyEntry : nestedCurrencyMap.entrySet()) {
			String currencyCode = currencyEntry.getKey();
			Map<String, Double> innerCurrencyData = currencyEntry.getValue();
			double rate = innerCurrencyData.get("rate");

			
			// Attributes are then added to static fxRates Map, with the key set as the currency code and the rate as the value
			addToFXRates(currencyCode, rate);
		}

	}
}
