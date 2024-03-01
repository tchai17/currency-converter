package com.fdmgroup.ood3_timothy.chai;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class TransactionsProcessor {
	
	public TransactionsProcessor() {
		
	}
	
	public void executeTransaction(String transaction) {
		
	}
	
	public void updateUsersFile() throws StreamReadException, DatabindException, IOException {
		List<User> userList = readUsersFile();
		
	}
	
	private List<User> readUsersFile() throws StreamReadException, DatabindException, IOException {
		ObjectMapper userMapper = new ObjectMapper();
		TypeReference <List<User>> userListType = new TypeReference<>(){};
		File usersFile = new File(Runner.getFilePath("users.json"));
		return userMapper.readValue(usersFile, userListType);
	}
	
}
