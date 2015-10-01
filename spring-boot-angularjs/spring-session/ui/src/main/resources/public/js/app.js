angular
		.module('app', [ 'ngRoute' ])

		.config(
				function($routeProvider, $httpProvider) {

					$routeProvider.when('/', {
						templateUrl : 'home.html',
						controller : 'home'
					}).when('/login', {
						templateUrl : 'login.html',
						controller : 'navigation'
					}).otherwise('/');

					$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

					// The custom "X-Requested-With" is a conventional header
					// sent by browser
					// clients,
					// and it used to be the default in Angular but they took it
					// out in 1.3.0.
					// Spring Security
					// responds to it by not sending a "WWW-Authenticate" header
					// in a 401
					// response,
					// and thus
					// the browser will not pop up an authentication dialog
					// (which is desirable
					// in
					// our app
					// since we want to control the authentication, yet still
					// using Basic
					// Authentication).
				})

		// Sending a Custom Token from the UI
		//
		// The only missing piece is the transport mechanism for the key to the
		// data in the store.
		// The key is the HttpSession ID, so if we can get hold of that key in
		// the UI client, we can
		// send it as a custom header to the resource server. So the "home"
		// controller would need to change
		// so that it sends the header as part of the HTTP request for the
		// greeting resource:

		// (A more elegant solution might be to grab the token as needed, and
		// use an Angular interceptor to add the header to every request to the
		// resource server. The interceptor definition could then be abstracted
		// instead of doing it all in one place and cluttering up the business
		// logic.)

		// Instead of going directly to
		// "http://localhost:9000" we have wrapped that
		// call in the success callback of a call to a new custom endpoint on
		// the UI server at "/token". The implementation of that is trivial:

		.controller('home', function($scope, $http) {
			$http.get('token').success(function(token) {
				$http({
					url : 'http://localhost:9000',
					method : 'GET',
					headers : {
						'X-Auth-Token' : token.token
					}
				}).success(function(data) {
					$scope.greeting = data;
				});
			})
		})

		.controller(
				'navigation',
				function($rootScope, $scope, $http, $location, $route) {

					$scope.tab = function(route) {
						return $route.current
								&& route === $route.current.controller;
					};
					var authenticate = function(credentials, callback) {

						var headers = credentials ? {
							authorization : "Basic "
									+ btoa(credentials.username + ":"
											+ credentials.password)
						} : {};

						$http.get('user', {
							headers : headers
						}).success(function(data) {
							if (data.name) {
								$rootScope.authenticated = true;
							} else {
								$rootScope.authenticated = false;
							}
							callback && callback();
						}).error(function() {
							$rootScope.authenticated = false;
							callback && callback();
						});

					}

					authenticate();

					$scope.credentials = {};
					// LOGIN FUNCTION
					$scope.login = function() {
						authenticate($scope.credentials, function() {
							if ($rootScope.authenticated) {
								console.log("Login succeeded")
								$location.path("/");
								$scope.error = false;
							} else {
								console.log("Login failed")
								$location.path("/login");
								$scope.error = true;
							}
						});
					};
					// LOGOUT FUNCTION
					// After "logout" Spring Security redirects to "/login?logout"
					// by default, yet this causes an error on the client: "http://localhost:8080/login?logout 401 (Unauthorized)"
					//
					// The redirect to /login isn't needed in this case so the
					// client error is ignorable. If you want to tidy up the
					// client you might be able to ask it not to follow the
					// redirect. Or just provide a /login endpoint with some
					// really basic content.
					$scope.logout = function() {
						$http.post('logout', {}).success(function() {
							$rootScope.authenticated = false;
							$location.path("/");
						}).error(function(data) {
							console.log("Logout failed")
							$rootScope.authenticated = false;
						});
					}
				});

// All of the code in the "navigation" controller will be executed when the page
// loads because the <div>
// containing the menu bar is visible and is decorated with
// ng-controller="navigation". In addition to
// initializing the credentials object, it defines 2 functions, the login() that
// we need in the form, and
// a local helper function authenticate() which tries to load a "user" resource
// from the backend. The
// authenticate() function is called when the controller is loaded to see if the
// user is actually already
// authenticated (e.g. if he had refreshed the browser in the middle of a
// session). We need the authenticate()
// function to make a remote call because the actual authentication is done by
// the server, and we don’t want
// to trust the browser to keep track of it.
