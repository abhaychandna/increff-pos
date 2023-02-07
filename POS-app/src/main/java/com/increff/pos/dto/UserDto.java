package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;

@Component
public class UserDto {
    @Autowired
	private InfoData info;
    @Autowired
    private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;


    public ModelAndView signup(HttpServletRequest request, SignupForm form) throws ApiException {
        try {
            validate(form);
            UserPojo user = ConvertUtil.convert(form, UserPojo.class);
            userService.add(user);
			LoginForm loginForm = ConvertUtil.convert(form, LoginForm.class);
			return login(request, loginForm);
        }
        catch (ApiException e) {
            info.setMessage(e.getMessage());
            return new ModelAndView("redirect:/site/signup");
        }
    }

    public ModelAndView login(HttpServletRequest request, LoginForm form) throws ApiException {
		UserPojo user = userService.get(form.getEmail());
		boolean authenticated = (Objects.nonNull(user) && passwordEncoder.matches(form.getPassword(), user.getPassword()));
		if (!authenticated) {
			info.setMessage("Invalid username or password");
			return new ModelAndView("redirect:/site/login");
		}

		// Create authentication object
		Authentication authentication = convert(user);
		// Create new session
		HttpSession session = request.getSession(true);
		// Attach Spring SecurityContext to this new session
		SecurityUtil.createContext(session);
		// Attach Authentication object to the Security Context
		SecurityUtil.setAuthentication(authentication);

		info.setMessage("");
        return new ModelAndView("redirect:/ui/home");

    }

	public ModelAndView logout(HttpServletRequest request) {
		request.getSession().invalidate();
		info.setMessage("");
		return new ModelAndView("redirect:/site/login");
	}
    
    private Authentication convert(UserPojo user) {
		// Create principal
		UserPrincipal principal = new UserPrincipal();
		principal.setRole(user.getRole());
		principal.setEmail(user.getEmail());
		principal.setId(user.getId());

		// Create Authorities
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(String.valueOf(user.getRole())));

		// Create Authentication
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
				authorities);
		return token;
	}

	private void validate(SignupForm form) throws ApiException {
		PreProcessingUtil.validate(form);
		validateEmail(form.getEmail());
		validatePassword(form.getPassword());
	}

	private void validateEmail(String email) throws ApiException {
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		String errorMessage = "Invalid email format";
		if (!email.matches(emailRegex)) {
			throw new ApiException(errorMessage);
		}
	}

	private void validatePassword(String password) throws ApiException {
		if (password.length() < 8) {
			throw new ApiException("Password must be at least 8 characters");
		}
	}

}
