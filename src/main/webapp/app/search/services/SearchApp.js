var searchModule = angular.module('searchModule', ['ngRoute', 'ngCookies', 'ui.bootstrap']);

searchModule.config(function($routeProvider, $locationProvider, $httpProvider) {			
			
			//Route provider configuration
			$routeProvider.when('/search', {
				templateUrl: 'app/search/views/search_task.html',
				controller: SearchController
			});
			
			$routeProvider.when('/login', {
				templateUrl: 'web/user/login',
				controller: SearchController
			});
			/* Register error provider that shows message on failed requests or redirects to login page on
			 * unauthenticated requests */
		    $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
			        return {
			        	'responseError': function(rejection) {
			        		var status = rejection.status;
			        		var config = rejection.config;
			        		var method = config.method;
			        		var url = config.url;
			      
			        		if (status == 401 || status == 403) {
			        			$location.path( "/login" );
			        		} else {
			        			$rootScope.error = method + " on " + url + " failed with status " + status;
			        		}
			              
			        		return $q.reject(rejection);
			        	}
			        };
			    }
		    );
			
	
});

//Login validation. 
//!!!!!! ATTENTION !!!!!!!
searchModule.run(function($rootScope, $location, $http) {
	
	if ($rootScope.user == undefined || $rootScope.user == null) {
		$http.get('web/user/userdetails').success(function(response) {
			if (response.status == "OK") {
				$rootScope.user = response.payLoad;
			} else {
				$location.path("/login");
			}
		}).error(function(response) {
			console.log("Error Occurred");
	    });
	} 
});