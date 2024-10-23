package com.spring.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.spring.entity.UserData;
import com.spring.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserService userService;


	public Boolean sendEmailForPasswordReset(String url, String reciepentEmail)
			throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("shikharc245@gmail.com", "PlayPal Team");
		helper.setTo(reciepentEmail);

//      previous code
//		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
//				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
//				+ "\">Change my password</a></p>";

		String content = "<html>" + "<body>" + "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>To change your password, please click the link below:</p>" + "<p>"
				+ "<a href=\"" + url
				+ "\" style='text-decoration: none; color: white; background-color: #007BFF; padding: 10px 15px; border-radius: 5px;'>Change my password</a>"
				+ "</p>" + "<p>If you did not request this change, please ignore this email.</p>"
				+ "<p>Thank you,<br>PlayPal Team</p>" + "</body>" + "</html>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}


	public void sendEmailForAccountVerification(UserData user, String url) {

		String from = "shikharc245@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verification";
//previous code for reference
//		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
//				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "Admin";

		String content = "<html>" + "<body>" + "<h2>Account Verification</h2>" + "<p>Dear [[name]],</p>"
				+ "<p>Thank you for registering! To complete your registration, please click the link below to verify your account:</p>"
				+ "<h3><a href='[[URL]]' target='_self' style='text-decoration: none; color: white; background-color: #007BFF; padding: 10px 15px; border-radius: 5px;'>VERIFY</a></h3>"
				+ "<p>If you did not request this email, please ignore it.</p>" + "<p>Thank you,<br>PlayPal Team</p>"
				+ "</body>" + "</html>";

		try {

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(from, "PlayPal Team");
			helper.setTo(to);
			helper.setSubject(subject);

			content = content.replace("[[name]]", user.getName());
			String siteUrl = url + "/verify?code=" + user.getVerificationCode();

			System.out.println(siteUrl);

			content = content.replace("[[URL]]", siteUrl);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static String generateUrl(HttpServletRequest request) {
		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}

	public UserData getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserData userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

}
