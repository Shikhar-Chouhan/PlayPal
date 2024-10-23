package com.spring.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.spring.entity.UserData;
import com.spring.repository.UserRepository;
import com.spring.service.UserService;
import com.spring.util.AppConstant;
import com.spring.util.CommonUtil;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	public UserData saveUser(UserData user, String url) {
		user.setRole("ROLE_USER");
		user.setIsEnable(false);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setVerificationCode(UUID.randomUUID().toString());
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserData saveUser = userRepository.save(user);
		if (saveUser != null) {
			commonUtil.sendEmailForAccountVerification(saveUser, url);
		}
		return saveUser;
	}

	@Override
	public UserData saveAdmin(UserData user, String url) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(false);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setVerificationCode(UUID.randomUUID().toString());
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserData saveUser = userRepository.save(user);
		if (saveUser != null) {
			commonUtil.sendEmailForAccountVerification(saveUser, url);
		}
		return saveUser;
	}

	@Override
	public boolean verifyAccount(String verificationCode) {

		UserData user = userRepository.findByVerificationCode(verificationCode);
		if (user == null) {
			return false;
		} else {
			user.setIsEnable(true);
			user.setVerificationCode(null);
			userRepository.save(user);
			return true;
		}

	}

	@Override
	public void resetAttempt(String email) {
		UserData user = userRepository.findByEmail(email);
		user.setFailedAttempt(0);
		userRepository.save(user);
	}

	@Override
	public UserData getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserData> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {

		Optional<UserData> findByuser = userRepository.findById(id);

		if (findByuser.isPresent()) {
			UserData UserDetails = findByuser.get();
			UserDetails.setIsEnable(status);
			userRepository.save(UserDetails);
			return true;
		}

		return false;
	}

	@Override
	public void increaseFailedAttempt(UserData user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
	}

	@Override
	public void userAccountLock(UserData user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserData user) {

		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

		long currentTime = System.currentTimeMillis();

		if (unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}

		return false;
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserData findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}

	@Override
	public UserData getUserByToken(String token) {
		return userRepository.findByResetToken(token);
	}

	@Override
	public UserData updateUser(UserData user) {
		return userRepository.save(user);
	}

	// previous code

	@Override
	public UserData updateUserProfile(UserData user, MultipartFile img) {

		UserData dbUser = userRepository.findById(user.getId()).get();

		if (!img.isEmpty()) {
			dbUser.setProfileImage(img.getOriginalFilename());
		}

		if (!ObjectUtils.isEmpty(dbUser)) {

			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser = userRepository.save(dbUser);
		}

		try {
			if (!img.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ img.getOriginalFilename());
								
				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	@Override
	public void deleteUserById(Integer id) {
		userRepository.deleteById(id);
	}

}
