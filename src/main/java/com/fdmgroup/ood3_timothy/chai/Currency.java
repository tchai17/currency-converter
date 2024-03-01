package com.fdmgroup.ood3_timothy.chai;

import com.fasterxml.jackson.annotation.JsonProperty;

class Currency {
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("rate")
	private double rate;
	
	public Currency() {
		
	}
	
	public Currency(String code, double rate) {
		this.code = code;
		this.rate = rate;
	}


	public String getCode() {
		return code;
	}

	public double getRate() {
		return rate;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}
	
	
}
