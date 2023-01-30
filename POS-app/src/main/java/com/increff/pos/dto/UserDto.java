package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.InfoData;
import com.increff.pos.model.LoginForm;
import com.increff.pos.model.SignupForm;
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


    public ModelAndView signup(SignupForm form) throws ApiException {
        try {
            normalizeAndValidate(form);
            UserPojo p = ConvertUtil.convert(form, UserPojo.class);
            userService.add(p);
			info.setMessage("");
            return new ModelAndView("redirect:/site/login");
        }
        catch (ApiException e) {
            info.setMessage(e.getMessage());
            return new ModelAndView("redirect:/site/signup");
        }
    }

    public ModelAndView login(HttpServletRequest request, LoginForm form) throws ApiException {
		UserPojo user = userService.get(form.getEmail());
		boolean authenticated = (Objects.nonNull(user) && Objects.equals(user.getPassword(), form.getPassword()));
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

        return new ModelAndView("redirect:/ui/home");

    }
    
    private static Authentication convert(UserPojo p) {
		// Create principal
		UserPrincipal principal = new UserPrincipal();
		principal.setRole(p.getRole());
		principal.setEmail(p.getEmail());
		principal.setId(p.getId());

		// Create Authorities
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(p.getRole()));
		// you can add more roles if required

		// Create Authentication
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
				authorities);
		return token;
	}

	private void normalizeAndValidate(SignupForm form) throws ApiException {
		PreProcessingUtil.normalizeAndValidate(form);
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
