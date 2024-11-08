package com.spring.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.entity.UserData;
import com.spring.service.UserService;
import com.spring.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserData userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
	}

	@GetMapping("/")
	public String index(Model m) {
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserData user, @RequestParam("img") MultipartFile file,
			HttpServletRequest request, HttpSession session) throws IOException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg",
					"The email address provided already exists. Please use a different email or try to signin your account.");
		} else {
			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			String url = CommonUtil.generateUrl(request);
			UserData saveUser = userService.saveUser(user, url);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

					// System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("succMsg",
						"Registration completed successfully. Please check your email, as a verification link will be sent shortly.");
			} else {
				session.setAttribute("errorMsg",
						"An error occurred on the server. Please try again later or contact support if the issue persists.");
			}
		}

		return "redirect:/register";
	}

	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model m, HttpSession session) {
		boolean f = userService.verifyAccount(code);
		if (f) {
			session.setAttribute("succMsg",
					"Your account has been successfully verified. Thank you! You may now proceed to signin.");
		} else {
			session.setAttribute("errorMsg",
					"The verification link has either expired or your account is already verified. If you need further assistance, please contact support.");

		}
		return "redirect:/register";
	}

//	Forgot Password Code
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		UserData userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "The email address entered is invalid. Please check and try again.");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendEmailForPasswordReset(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email. A password reset link will be sent shortly.");
			} else {
				session.setAttribute("errorMsg",
						"An error occurred on the server. Please try again later or contact support if the issue persists.");
			}
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		UserData userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			session.setAttribute("errorMsg",
					"The link you provided is either invalid or has expired. Please request a new link if needed.");
			return "redirect:/forgot-password";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		UserData userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			session.setAttribute("errorMsg",
					"The link you provided is either invalid or has expired. Please request a new link if needed.");
			return "redirect:/forgot-password";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			return "redirect:/signin?success=Your+password+has+been+changed+successfully.";
		}
	}
	
	//new code
	@GetMapping("/learn")
	public String learn() {
		return "learn";
	}
	
	@GetMapping("/contactus")
	public String contactus() {
		return "contactus";
	}

}
