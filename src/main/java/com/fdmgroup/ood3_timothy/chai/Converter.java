package com.fdmgroup.ood3_timothy.chai;

class Converter {

	public static double convert(String fromCurrency, String toCurrency, double fromCurrencyRate) {
		double toCurrencyRate = 0.01;
		if ( toCurrency.equalsIgnoreCase("usd") ) {
			toCurrencyRate = 1;
		}
		else {
			toCurrencyRate = FXRates.fxRates.get(toCurrency);
			
		}
		
		double actualCurrencyRate = fromCurrencyRate / toCurrencyRate;
		
		return actualCurrencyRate;
		
		
	}
	
	
}
