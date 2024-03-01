package com.fdmgroup.ood3_timothy.chai;


import java.util.TreeMap;

class User {
	private String name;
	private TreeMap<Currency, Double> wallet;

	/**
	 * Default, no-args constructor for Jackson
	 */
	public User() {

	}

	/**
	 * Custom constructor
	 * 
	 * @param name   User's name
	 * @param wallet
	 */
	public User(String name, TreeMap<Currency, Double> wallet) {
		this.name = name;
		this.wallet = wallet;
	}

	public String getName() {
		return name;
	}

	public TreeMap<Currency, Double> getWallet() {
		return wallet;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWallet(TreeMap<Currency, Double> wallet) {
		this.wallet = wallet;
	}

}
