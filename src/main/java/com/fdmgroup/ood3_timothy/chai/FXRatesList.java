package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FXRatesList {
	static List<Currency> currencyRateList = new ArrayList<>();

	public static void addToCurrencyRateList(Currency currency) {
		currencyRateList.add(currency);
	}
	
	public static void initializeFXratesToListFromFile(String filepath) throws StreamReadException, DatabindException, IOException {
		
		File fxFile = new File(filepath);
		ObjectMapper currencyMapper = new ObjectMapper();
		
		Map < String, Map < String, Double > > nestedCurrencyMap = currencyMapper.readValue(fxFile, Map.class );
		for ( Map.Entry< String, Map < String, Double > > currencyEntry : nestedCurrencyMap.entrySet() ) {
			String currencyCode = currencyEntry.getKey();
			Map<String, Double> innerCurrencyData = currencyEntry.getValue();
			double rate = innerCurrencyData.get("rate");
			
			Currency currency = new Currency(currencyCode, rate);
			addToCurrencyRateList(currency);
		}
		
	}
	
}
