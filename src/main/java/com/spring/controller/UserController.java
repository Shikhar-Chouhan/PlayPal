package com.spring.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.entity.UserData;
import com.spring.service.UserService;
import com.spring.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserData userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
	}

	private UserData getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserData userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

	@GetMapping("/profile")
	public String profile() {
		return "/user/profile";
	}


	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserData user, @RequestParam MultipartFile img, HttpSession session) {
		UserData updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "An error occurred on the server. Please try again later or contact support if the issue persists.");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserData loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserData updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "An error occurred on the server. Please try again later or contact support if the issue persists.");
			} else {
				session.setAttribute("succMsg", "Your password has been successfully updated.");
			}
		} else {
			session.setAttribute("errorMsg", "The current password entered is incorrect. Please try again.");
		}

		return "redirect:/user/profile";
	}
	
	@PostMapping("/delete-account")
	public String deleteAccount(@RequestParam("id") Integer id, HttpSession session) {
		try {
			userService.deleteUserById(id);
			session.invalidate(); // Invalidate session after deleting account
			return "redirect:/signin?success=Your Account has been successfully Deleted."; // Redirect to login after deletion
		} catch (Exception e) {
			session.setAttribute("errorMsg", "An error occurred while trying to delete the account. Please try again.");
			return "redirect:/user/profile";
		}
	}

}
