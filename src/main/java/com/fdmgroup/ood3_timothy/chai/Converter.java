package com.fdmgroup.ood3_timothy.chai;

class Converter {

	public double convert(String fromCurrency, String toCurrency, double fromCurrencyRate) {
		double toCurrencyRate = 0.01;
		if ( toCurrency.equalsIgnoreCase("usd") ) {
			toCurrencyRate = 1;
		}
		else {
			for ( Currency currency : FXRatesList.currencyRateList ) {
				if ( currency.getCode().equalsIgnoreCase(toCurrency) ) {
					toCurrencyRate = currency.getRate();
				}
			}
		}
		
		double actualCurrencyRate = fromCurrencyRate / toCurrencyRate;
		
		return actualCurrencyRate;
		
		
	}
	
	
}
