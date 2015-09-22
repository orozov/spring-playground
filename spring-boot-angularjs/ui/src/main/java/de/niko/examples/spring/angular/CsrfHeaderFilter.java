package de.niko.examples.spring.angular;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * So on the server we need a custom filter that will send the cookie. Angular
 * wants the cookie name to be "XSRF-TOKEN" and Spring Security provides it as a
 * request attribute, so we just need to transfer the value from a request
 * attribute to a cookie
 * 
 * @author niko
 *
 */
public class CsrfHeaderFilter extends OncePerRequestFilter {

	private static final String XSRF_TOKEN = "XSRF-TOKEN";

	/**
	 * o finish the job and make it completely generic we should be careful to
	 * set the cookie path to the context path of the application (instead of
	 * hard-coded to "/"), but this is good enough for this application.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrf != null) {
			Cookie cookie = WebUtils.getCookie(request, XSRF_TOKEN);
			String token = csrf.getToken();
			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie(XSRF_TOKEN, token);
				cookie.setPath(request.getContextPath()); // setPath("/");
				response.addCookie(cookie);
			}
		}
		filterChain.doFilter(request, response);
	}
}
