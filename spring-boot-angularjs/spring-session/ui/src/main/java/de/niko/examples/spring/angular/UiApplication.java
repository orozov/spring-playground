package de.niko.examples.spring.angular;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@RestController
@EnableRedisHttpSession
public class UiApplication {

	/**
	 * This is a useful trick in a Spring Security application. If the "/user"
	 * resource is reachable then it will return the currently authenticated
	 * user (an Authentication), and otherwise Spring Security will intercept
	 * the request and send a 401 response through an AuthenticationEntryPoint.
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping("/token")
	public Map<String, String> token(HttpSession session) {
		// System.out.println("Session token: " + session.getId());
		return Collections.singletonMap("token", session.getId());
	}
	
	/**
	 * It is not adequate for CSRF protection to rely on a cookie being sent
	 * back to the server because the browser will automatically send it even if
	 * you are not in a page loaded from your application (a Cross Site
	 * Scripting attack, otherwise known as XSS). The header is not
	 * automatically sent, so the origin is under control. You might see that in
	 * our application the CSRF token is sent to the client as a cookie, so we
	 * will see it being sent back automatically by the browser, but it is the
	 * header that provides the protection.
	 * 
	 * @author niko
	 *
	 */
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and().authorizeRequests()
					.antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll().anyRequest()
					.authenticated().and().logout().and().csrf().csrfTokenRepository(csrfTokenRepository()).and()
					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}

		/**
		 * The other thing we have to do on the server is tell Spring Security
		 * to expect the CSRF token in the format that Angular wants to send it
		 * back (a header called "X-XRSF-TOKEN" instead of the default
		 * "X-CSRF-TOKEN"). We do this by customizing the CSRF filter
		 * 
		 * @return
		 */
		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}

		/**
		 * To finish the job and make it completely generic we should be careful
		 * to set the cookie path to the context path of the application
		 * (instead of hard-coded to "/"), but this is good enough for this
		 * application.
		 * 
		 * @return
		 */
		private Filter csrfHeaderFilter() {

			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
						FilterChain filterChain) throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
						String token = csrf.getToken();
						if (cookie == null || token != null && !token.equals(cookie.getValue())) {
							cookie = new Cookie("XSRF-TOKEN", token);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					filterChain.doFilter(request, response);
				}
			};
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}
}
