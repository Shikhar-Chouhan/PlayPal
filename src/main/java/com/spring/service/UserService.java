package com.spring.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.spring.entity.UserData;


public interface UserService {


	public UserData getUserByEmail(String email);

	public List<UserData> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	public void increaseFailedAttempt(UserData user);

	public void userAccountLock(UserData user);

	public boolean unlockAccountTimeExpired(UserData user);

	public void updateUserResetToken(String email, String resetToken);

	public UserData getUserByToken(String token);

	public UserData updateUser(UserData user);

	public UserData updateUserProfile(UserData user, MultipartFile img);

	public UserData saveAdmin(UserData user,String url);

	public Boolean existsEmail(String email);

	//for the email verification
	public UserData saveUser(UserData user,String url);

	public boolean verifyAccount(String verificationCode);

	public void resetAttempt(String email);
	
	public void deleteUserById(Integer id);
}
