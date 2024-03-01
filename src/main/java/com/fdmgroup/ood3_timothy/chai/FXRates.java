package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FXRates {

	static Map<String, Double> fxRates = new TreeMap<>();

	public static void addToFXRates(String code, Double rate) {
		fxRates.put(code, rate);
	}

	public static void initializeFXratesToMapFromFile() throws StreamReadException, DatabindException, IOException {

		File fxFile = new File(Runner.getFilePath("fx_rates.json"));
		ObjectMapper currencyMapper = new ObjectMapper();

		Map<String, Map<String, Double>> nestedCurrencyMap = currencyMapper.readValue(fxFile, Map.class);
		for (Map.Entry<String, Map<String, Double>> currencyEntry : nestedCurrencyMap.entrySet()) {
			String currencyCode = currencyEntry.getKey();
			Map<String, Double> innerCurrencyData = currencyEntry.getValue();
			double rate = innerCurrencyData.get("rate");

			addToFXRates(currencyCode, rate);
		}

	}
}
