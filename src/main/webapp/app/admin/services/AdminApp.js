var adminModule = angular.module('adminModule', ['ngRoute', 'ui.bootstrap']);

adminModule.config(function($routeProvider, $locationProvider, $httpProvider) {			
	
		//Route provider configuration
		$routeProvider.when('/dashboard', {
			templateUrl: 'web/admin/dashboard',
			controller: AdminController
		});
		$routeProvider.when('/user_config', {
            templateUrl: 'web/admin/user_config',
            controller: UserConfigController
        });	
		$routeProvider.when('/client_config', {
            templateUrl: 'web/admin/client_config',
            controller: UserConfigController
        });
		$routeProvider.when('/charttype_chartspl_config', {
            templateUrl: 'web/admin/charttype_chartspl_config',
            controller: UserConfigController
        });	
		$routeProvider.when('/user_client_charttype/:userId', {
            templateUrl: 'app/admin/views/confightml/user_client_charttype_config.html',
            controller: UserConfigController
        });	
		$routeProvider.when('/login', {
            templateUrl: 'web/user/login',
            controller: AdminController
        });	
		
		$routeProvider.otherwise({redirectTo: '/user_config'});
			
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
adminModule.run(function($rootScope, $location, $http) {
	
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

adminModule.service('AdminSearchService',function ($filter) {

    this.searchByName = function(searchItems, queryName) {
        return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
                if (searchMatch(item['name'], queryName)) {
                    return true;
                }
            }
            return false;
        });

    };
	
	this.searchByClient = function(searchItems, queryClient) {
        return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
                if (searchMatch(item['client'], queryClient)) {
                    return true;
                }
            }
            return false;
        });

    };
	this.searchByStatus = function(searchItems, queryStatus) {
	    return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['status'], queryStatus)) {
                    return true;
                }
            }
            return false;
        });

    };
	
    this.searchByChartType = function(searchItems, queryChartType) {
        return filteredItemsRole = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['chartType'], queryChartType)) {
                    return true;
                }
            }
            return false;
        });

    };
    
    this.search = function(items, query){
            return searchItems = $filter('filter')(items, function (item) {
                for(var attr in item) {
                    if (searchMatch(item[attr], query)) {
                        return true;
                    }
                }
                return false;
            });
    };

    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
		if(haystack != null) {
			if (typeof haystack == 'string' || haystack instanceof String) {
				return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
			}
			else {
				var haystackStr = haystack.toString();
				return haystackStr.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
			}
		} else {
			return false;
		}

    };
    
});

