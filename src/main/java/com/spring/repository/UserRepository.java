package com.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.entity.UserData;

public interface UserRepository extends JpaRepository<UserData, Integer> {

	public UserData findByEmail(String email);

	public List<UserData> findByRole(String role);

	public UserData findByResetToken(String token);

	public Boolean existsByEmail(String email);

	//for email verification use
	public UserData findByVerificationCode(String code);
}
