package com.spring.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.spring.entity.UserData;
import com.spring.repository.UserRepository;
import com.spring.service.UserService;
import com.spring.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String email = request.getParameter("username");
		String errorMessage = "Wrong password. Please try again.";

		UserData userDtls = userRepository.findByEmail(email);

		if (userDtls != null) {
			if (userDtls.getIsEnable()) {
				if (userDtls.getAccountNonLocked()) {
					if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
						userService.increaseFailedAttempt(userDtls);
					} else {
						userService.userAccountLock(userDtls);
						errorMessage = "Your account has been temporarily locked due to many unsuccessful login attempts.";
					}
				} else {
					if (userService.unlockAccountTimeExpired(userDtls)) {
						errorMessage = "Your account has been successfully unlocked. You may now sign in.";
					} else {
						errorMessage = "Your account is currently locked. Please try again later.";
					}
				}
			} else {
				errorMessage = "Your account is inactive. A verification link has been sent to your email. Please verify your account to proceed.";
			}
		} else {
			// If no user is found, this error will be shown
			errorMessage = "The Email you entered is invalid.";
		}

		request.getSession().setAttribute("errorMsg", errorMessage);

		// Redirect to the login page
		response.sendRedirect("/signin");
		
		
		
		/*this is just for reference of previous code
		 * String email = request.getParameter("username"); UserData userDtls =
		 * userRepository.findByEmail(email); if (userDtls != null) { if
		 * (userDtls.getIsEnable()) { if (userDtls.getAccountNonLocked()) { if
		 * (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
		 * userService.increaseFailedAttempt(userDtls); } else {
		 * userService.userAccountLock(userDtls); exception = new LockedException(
		 * "Your account has been temporarily locked due to many unsuccessful login attempts."
		 * ); } } else { if (userService.unlockAccountTimeExpired(userDtls)) { exception
		 * = new LockedException(
		 * "Your account has been successfully unlocked. You may now signin."); } else {
		 * exception = new LockedException(
		 * "Your account is currently locked. Please try again after sometime."); } } }
		 * else { exception = new LockedException(
		 * "Your account is inactive. A verification link has been sent to your email. Please check your inbox and verify your account to proceed."
		 * ); } } else { exception = new
		 * LockedException("The Email you entered is invalid."); }
		 * super.setDefaultFailureUrl("/signin?error");
		 * super.onAuthenticationFailure(request, response, exception);
		 */
	}

}
