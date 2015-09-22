# Spring Boot, Spring Security and AngularJS

Demo samples for [Angular JS](http://angularjs.org) with [Spring Security](http://projects.spring.io/spring-security). 

Things to consider are cookies, headers, native clients, various security vulnerabilities and how modern browser technology can help us to avoid them. In this series we show how nice features of the component frameworks can be integrated simply to provide a pleasant and secure user experience. Implementing back end resources and authentication to separate services, simple API Gateway on the front end implemented declaratively using Spring Cloud (show case for how to secure a javascript front end with a distributed back end). The example code uses Angular JS, but the same architecture and back ends can be used with any front-end stack.


All samples are based on the Dave Seyer's blog post series: [blog](http://spring.io/blog/2015/01/12/spring-and-angular-js-a-secure-single-page-application) and have the same basic functionality: a secure static, single-page application, which renders content from a secure back end JSON resource.  


Contents: 

* `UI`: sample with HTTP Basic authentication, static HTML and an API resource all in the same server

* `spring-session`: form based authentication and static HTML in one server ("ui") and a protected backend API resource in another ("resource"),  using [Spring Session](https://github.com/spring-projects/spring-session) as an authentication token between the UI and the back end service.

* `proxy`: the UI acting as a reverse proxy for the backend (API Gateway pattern). CORS responses are not needed because all client requests go to the same server. Authentication for the backend could be overlaid using the "spring-session" approach (above) or using "oauth2" (below)

* `oauth2`: same as "proxy" but with OAuth2 SSO to the UI and OAuth2 resource server protection for the backend. JWT tokens (signed, encoded JSON, carrying information about the user and the token grant) and a nice UI with a login screen in the authorization server.
