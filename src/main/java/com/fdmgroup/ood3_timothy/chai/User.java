package com.fdmgroup.ood3_timothy.chai;


import java.util.TreeMap;

class User {
	private String name;
	private TreeMap<String, Double> wallet;

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
