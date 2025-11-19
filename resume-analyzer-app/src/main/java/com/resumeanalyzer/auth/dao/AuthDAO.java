package com.resumeanalyzer.auth.dao;

import com.resumeanalyzer.auth.entity.User;

public interface AuthDAO {

	public User signUp(User user);
	
	public User getUserByUserName(String userName);
	
	public User getUserByEmail(String email);
	
	public User getUserById(Integer id);
	
	public User updateUserDetails(User userDetails);
	
	public void deleteUser(User user);
	
	public String changePassword(User user);
	
}
