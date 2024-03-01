package com.fdmgroup.ood3_timothy.chai;

/**
 * This class converts two USD-pegged rates to a relative actual rate comparing
 * both currencies, then calculates the amount in to-Currency In essence, abc /
 * usd and def / usd are converted to abc / def, or amount of $abc per 1.0 of
 * $def This rate is used to convert amount in from-Currency to to-Currency
 * 
 */
class Converter {

	public static double convert(String fromCurrency, String toCurrency, double fromCurrencyAmount) {

		// Define both rates outside conditional statements
		double fromCurrencyRate, toCurrencyRate;

		// if any of the currencies are usd, find the other and manipulate accordingly
		if (toCurrency.equalsIgnoreCase("usd")) {
			toCurrencyRate = 1;
			fromCurrencyRate = FXRates.fxRates.get(fromCurrency);
		} else if (fromCurrency.equalsIgnoreCase("usd")) {
			fromCurrencyRate = 1;
			toCurrencyRate = FXRates.fxRates.get(toCurrency);
		}

		// if both currencies are not usd, proceed to find both rates
		else {
			toCurrencyRate = FXRates.fxRates.get(toCurrency);
			fromCurrencyRate = FXRates.fxRates.get(fromCurrency);
		}

		// Calculate actual rates and actual amounts
		double actualCurrencyRate = fromCurrencyRate / toCurrencyRate;
		double toCurrencyAmount = fromCurrencyAmount / actualCurrencyRate;
		return toCurrencyAmount;

	}

}
