package com.spring.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserData userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserData> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType", type);
		m.addAttribute("users", users);
		return "/admin/users";
	}


	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserData user, @RequestParam("img") MultipartFile file,
			HttpServletRequest request, HttpSession session) throws IOException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());
		if (existsEmail) {
			session.setAttribute("errorMsg",
					"The email address provided already exists. Please use a different email or try to signin your account.");
		} else {
			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			String url = CommonUtil.generateUrl(request);
			UserData saveUser = userService.saveAdmin(user, url);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

//				System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("succMsg", "Registration completed successfully. Please check your email, as a verification link will be sent shortly.");
			} else {
				session.setAttribute("errorMsg",
						"An error occurred on the server. Please try again later or contact support if the issue persists.");
			}
		}
		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserData user, @RequestParam MultipartFile img, HttpSession session) {
		UserData updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg",
					"An error occurred on the server. Please try again later or contact support if the issue persists.");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/admin/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserData loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserData updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg",
						"An error occurred on the server. Please try again later or contact support if the issue persists.");
			} else {
				session.setAttribute("succMsg", "Your password has been successfully updated.");
			}
		} else {
			session.setAttribute("errorMsg", "The current password entered is incorrect. Please try again.");
		}

		return "redirect:/admin/profile";
	}
	
	@PostMapping("/delete-account")
	public String deleteAccount(@RequestParam("id") Integer id, HttpSession session) {
		try {
			userService.deleteUserById(id);
			session.invalidate(); // Invalidate session after deleting account
			return "redirect:/signin?success=Your Account has been successfully Deleted."; // Redirect to login after deletion
		} catch (Exception e) {
			session.setAttribute("errorMsg", "An error occurred while trying to delete the account. Please try again.");
			return "redirect:/admin/profile";
		}
	}

}
