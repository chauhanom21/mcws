var supervisorModule = angular.module('supervisorModule', ['ngRoute', 'restangular', 'ngCookies', 
                  'ngTable', 'xeditable', 'ui.bootstrap', 'ngDialog', 'commonServiceModule']);

supervisorModule.config(function($routeProvider, $locationProvider, $httpProvider, RestangularProvider) {			
		//Route provider configuration
		$routeProvider.when('/login', {
			templateUrl: 'web/user/login',
			controller: MonitorUserController
		});
		$routeProvider.when('/dashboard', {
            templateUrl: 'web/supervisor/dashboard',
            controller: SuperDashboardController
        });
		$routeProvider.when('/monitorUsers', {
            templateUrl: 'web/supervisor/monitorUsers',
            controller: MonitorUserController
        });		
		$routeProvider.when('/newupload', {
            templateUrl: 'web/supervisor/newupload',
            controller: AdminFileController
        });
		$routeProvider.when('/monitorSelectedUser/:userId', {
            templateUrl: 'web/supervisor/monitorSelectedUser',
            controller: MonitorPerUserController
        });		
		$routeProvider.when('/monitorTask', {
            templateUrl: 'web/supervisor/monitorTask',
            controller: MonitorTaskController
        });
        $routeProvider.when('/setupAuditing', {
            templateUrl: 'web/supervisor/setupAuditing',
            controller: AuditConfigController
        });
        $routeProvider.when('/deleteWorklistData', {
            templateUrl: 'web/supervisor/deleteWorklistData',
            controller: DeleteWorklistDataController
        });
        $routeProvider.when('/supervisor_report', {
            templateUrl: 'app/supervisor/views/reports/supervisor_report.html',
            controller: SupervisorReportsController
        });
        $routeProvider.when('/invoice_report', {
            templateUrl: 'app/supervisor/views/reports/invoice_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/clientwise_consolidation_report', {
            templateUrl: 'app/supervisor/views/reports/clientwise_consolidation_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/invoice_report', {
            templateUrl: 'app/supervisor/views/reports/invoice_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/daily_productivity_report_coder', {
            templateUrl: 'app/supervisor/views/reports/daily_productivity_report_coder.html',
            controller: ReportsController
        });
        $routeProvider.when('/daily_productivity_report_localqa', {
            templateUrl: 'app/supervisor/views/reports/daily_productivity_report_localqa.html',
            controller: ReportsController
        });
        $routeProvider.when('/filewise_tat_report', {
            templateUrl: 'app/supervisor/views/reports/filewise_tat_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/localQA_tracking_report', {
            templateUrl: 'app/supervisor/views/reports/localQA_tracking_report.html',
            controller: ReportsController
        }); //#coderwise_tracking_report
        $routeProvider.when('/coderwise_tracking_report', {
            templateUrl: 'app/supervisor/views/reports/coderwise_tracking_report.html',
            controller: ReportsController
        });
        
        $routeProvider.when('/coderCNRreport', {
            templateUrl: 'app/supervisor/views/reports/coder_cnr_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/qaCNRreport', {
            templateUrl: 'app/supervisor/views/reports/qa_cnr_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/CoderCNRReportDetail', {
            templateUrl: 'app/supervisor/views/reports/coder_cnr_reportdetail.html',
            controller: ReportsController
        });
        $routeProvider.when('/QACNRReportDetail', {
            templateUrl: 'app/supervisor/views/reports/qa_cnr_reportdetail.html',
            controller: ReportsController
        });
        $routeProvider.when('/coderQualityReport', {
            templateUrl: 'app/supervisor/views/reports/coder_quality_report.html',
            controller: ReportsController
        });
        $routeProvider.when('/miscReport', {
            templateUrl: 'app/supervisor/views/reports/misc_report.html',
            controller: ReportsController
        });
        
        //Route provider configuration for search
		$routeProvider.when('/search', {
			templateUrl: 'app/search/views/search_task.html',
			controller: SearchController
		});
		$routeProvider.otherwise({redirectTo: '/monitorUsers'});
		
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
supervisorModule.run(function($rootScope, $location, $http) {
	
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



supervisorModule.service('SortService', function () {

    this.sortByKeyAsc = function(array, key) {
        return array.sort(function(a, b) {
            var x = (a[key].trim()).toLowerCase(); 
            var y = (b[key].trim()).toLowerCase();
            return ((x < y) ? -1 : ((x > y) ? 1 : 0));
        });
    }
    
    this.sortByKeyDesc = function(array, key) {
        return array.sort(function(a, b) {
        	var x = (a[key].trim()).toLowerCase(); 
            var y = (b[key].trim()).toLowerCase();
            return ((y < x) ? -1 : ((y > x) ? 1 : 0));
        });
    }
    
});
