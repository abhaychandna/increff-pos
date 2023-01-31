package com.increff.pos.util;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

	public static String getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (Objects.isNull(cookies)) {
			return null;
		}
		for (Cookie c : cookies) {
			if (name.equals(c.getName())) {
				return c.getValue();
			}
		}

		return null;
	}
}
