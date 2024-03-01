package com.fdmgroup.ood3_timothy.chai;

import java.util.TreeMap;

/**
 * The User class is designed as a format for Jackson deserialization.
 * 
 * @param name   String which sets the name of the User object
 * @param wallet TreeMap which stores currency and amount
 * 
 */
class User {
	private String name;
	private TreeMap<String, Double> wallet;

	/**
	 * Default, no-args constructor for Jackson
	 */
	public User() {

	}

	/**
	 * Custom constructor for instantiation of the User class
	 * 
	 * @param name   User's name
	 * @param wallet TreeMap which stores the currency and the amount of said
	 *               currency
	 */
	public User(String name, TreeMap<String, Double> wallet) {
		this.name = name;
		this.wallet = wallet;
	}

	public String getName() {
		return name;
	}

	public TreeMap<String, Double> getWallet() {
		return wallet;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWallet(TreeMap<String, Double> wallet) {
		this.wallet = wallet;
	}

}
