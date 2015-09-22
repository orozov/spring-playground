angular.module('app', [ 'ngRoute' ])

.config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation'
	}).otherwise('/');

	 $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
	
	// The custom "X-Requested-With" is a conventional header sent by browser
	// clients,
	// and it used to be the default in Angular but they took it out in 1.3.0.
	// Spring Security
	// responds to it by not sending a "WWW-Authenticate" header in a 401 response,
	// and thus
	// the browser will not pop up an authentication dialog (which is desirable in
	// our app
	// since we want to control the authentication, yet still using Basic Authentication).
})

.controller('home', function($scope, $http) {
	$http.get('/resource/').success(function(data) {
		$scope.greeting = data;
	})
})

.controller('navigation', function($rootScope, $scope, $http, $location) {

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
						$location.path("/");
						$scope.error = false;
					} else {
						$location.path("/login");
						$scope.error = true;
					}
				});
			};
			// LOGOUT FUNCTION
			$scope.logout = function() {
				  $http.post('logout', {}).success(function() {
				    $rootScope.authenticated = false;
				    $location.path("/");
				  }).error(function(data) {
				    $rootScope.authenticated = false;
				  });
				}
		});

//All of the code in the "navigation" controller will be executed when the page loads because the <div> 
//containing the menu bar is visible and is decorated with ng-controller="navigation". In addition to 
//initializing the credentials object, it defines 2 functions, the login() that we need in the form, and 
//a local helper function authenticate() which tries to load a "user" resource from the backend. The 
//authenticate() function is called when the controller is loaded to see if the user is actually already 
//authenticated (e.g. if he had refreshed the browser in the middle of a session). We need the authenticate() 
//function to make a remote call because the actual authentication is done by the server, and we don’t want 
//to trust the browser to keep track of it.