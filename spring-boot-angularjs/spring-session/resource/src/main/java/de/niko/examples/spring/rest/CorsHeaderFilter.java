package de.niko.examples.spring.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.jndi.local.localContextRoot;
import org.neo4j.cypher.internal.compiler.v2_1.docbuilders.logicalPlanDocBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

/**
 * 
 * @author niko
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsHeaderFilter implements Filter {
	 private final Log logger = LogFactory.getLog(CorsHeaderFilter.class);

	/**
	 * CORS Negotiation
	 * 
	 * The browser tries to negotiate with our resource server to find out if it
	 * is allowed to access it according to the Cross Origin Resource Sharing
	 * protocol. It’s not an Angular JS responsibility, so just like the cookie
	 * contract it will work like this with all JavaScript in the browser. The
	 * two servers do not declare that they have a common origin, so the browser
	 * declines to send the request and the UI is broken.
	 * 
	 * To fix that we need to support the CORS protocol which involves a
	 * "pre-flight" OPTIONS request and some headers to list the allowed
	 * behaviour of the caller. Spring 4.2 might have some nice fine-grained
	 * CORS support, but until that is released we can do an adequate job for
	 * the purposes of this application by sending the same CORS responses to
	 * all requests using a Filter. We can just create a class in the same
	 * directory as the resource server application and make sure it is
	 * a @Component (so it gets scanned into the Spring application context),
	 * for example:
	 * 
	 * There is one tiny change to the resource server for it to be able to
	 * accept the custom header (x-auth-token). The CORS filter has to nominate
	 * that header as an allowed one from remote clients!
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "x-auth-token, x-requested-with");
		response.setHeader("Access-Control-Max-Age", "3600");

		if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
			filterChain.doFilter(req, res);
		} else {
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}
