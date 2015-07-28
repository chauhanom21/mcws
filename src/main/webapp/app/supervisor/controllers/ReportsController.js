'use strict';
/**
 * ReportsController
 * 
 * @param $scope
 * @param $http
 * @param $filter
 * @param $routeParams
 * @param $location
 * @param $modal
 * @param Restangular
 * @param SearchService
 */
function ReportsController($scope, $rootScope, $http, $filter, $routeParams, $location, $modal, Restangular, SearchService) {

	var dt = new Date();
	dt.setDate(dt.getDate() - 30);
	$(function () {
		$('#fromdatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: dt
		});
		
		$('#todatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date()
		});
	});

	$scope.disable_submit = false;
    $scope.$location = $location;
	$scope.clients = [];
	$scope.clientIds = [];
	$scope.chartTypes = [];
	$scope.fromDate = [];
    $scope.toDate = [];
    $scope.startDate = "";
    $scope.endDate = "";
    $scope.validateDate = false;
    $scope.reportResponse = [];
    $scope.status = {};
    //Gets the date value from date fields and assign to variable.
    $scope.initializeDateValues  = function() {
    	var fromDateObj = 0;
    	if($('#fromdatetime').data("DateTimePicker")) {
    		fromDateObj = $('#fromdatetime').data("DateTimePicker").getDate();
    	}
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	if(fromDateObj && toDateObj) {
    		if( fromDateObj > toDateObj ) {
        	    $(".ui-error-message").show();
        	    $scope.validateDate = false;
        	    return;
        	} else {
        		$(".ui-error-message").hide();
        		$scope.fromDate = fromDateObj.year() +"-" + (fromDateObj.month()+1) + "-" + fromDateObj.date();
        		$scope.toDate = toDateObj.year() +"-" + (toDateObj.month()+1) + "-" + toDateObj.date();
        		
        		$scope.startDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
        		$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
        	}
    		$scope.validateDate = true;
    		return;
    	}
    };
    
    //Gets the date value from todate field and assign to variable.
    $scope.initializeToDateValues  = function() {
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	if(toDateObj) {
    		$scope.toDate = toDateObj.year() +"-" + (toDateObj.month()+1) + "-" + toDateObj.date();
    		$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    	}
    };
	
	$scope.loadAllClients = function() {
		$http.get('web/client/detail/allClientsDetail').success(function(data) {
			$scope.clients = data.payLoad;
		});
	};
	
	
	$scope.loadAllChartTypes = function() {
		$http.get('web/client/detail/allChartTypes').success(function(data) {
			$scope.chartTypes = data.payLoad;
		});
	};
	
	/**
	 * This function will send request to load invoice Report of client
	 * By selected Date range 
	 */
    $scope.getInvoiceReportByClientAndDate = function(){
    	$scope.disable_submit = true;
    	$scope.initializeDateValues();
    	$scope.requestParam = {};
    	$scope.requestParam.clientId = $scope.queryClient;
    	$scope.requestParam.fromDate = $scope.fromDate;
    	$scope.requestParam.toDate = $scope.toDate;
		if($scope.validateDate) {
			var url = 'web/report/clientInvoiceReport?fromDate='+$scope.fromDate+"&toDate="+$scope.toDate+"&clientId="+$scope.queryClient;
    	
			$http.get(url).then(function(response) {			
				if (response.data.status == 'ERROR') {
					$scope.disable_submit = false;
					alert('No Data is available with the given date range !!');
					return;
				} else {
					$scope.downloadReports(url);
				}	
			});
			
		}
		return;
		
    };
    
    $scope.downloadReports = function(requetURL) {
    	$scope.disable_submit = false;
		// Start download
		 window.open(requetURL, '_blank');
		 window.focus();
    };
    
    $scope.generateClientConsolidationReport = function() {
    	$scope.disable_submit = true;
		$scope.initializeDateValues();
		var url = 'web/report/clientConsolidationReport?clients='+$scope.queryClient +'&fromDate='+$scope.fromDate +'&toDate='+$scope.toDate;
		$http.get(url).then(function(response) {			
			
			if (response.data.status == 'ERROR') {
				alert('No Data is available with the given date range !!');
				$scope.disable_submit = false;
				return;
			} else {
				$scope.downloadReports(url);
			}				
		});		
    };
    
    $scope.loadDailyProductivityReportDataForUser = function(reportType) {
    	$scope.disable_submit = true;
		$scope.initializeToDateValues();
		if($scope.toDate) {
			var url = 'web/report/dailyProductivityReport?toDate='+$scope.toDate+'&reportType='+reportType;
			
			$http.get(url).then(function(response) {			
				if (response.data.status == 'ERROR') {
					$scope.disable_submit = false;
					alert('No Data is available on the given Date !!');
					return;
				} else {
					$scope.downloadReports(url);
				}				
			});
			
    	}	
    };
    
    $scope.generateFilewiseTATReportData = function() {
    	$scope.disable_submit = true;
		$scope.initializeDateValues();

		var url = 'web/report/filewiseTATReport?fromDate='+$scope.fromDate +'&toDate='+$scope.toDate;
		
		$http.get(url).then(function(response) {			
			if (response.data.status == 'ERROR') {
				$scope.disable_submit = false;
				alert('No Data is available with the given date range !!');
				return;
			} else {
				$scope.downloadReports(url);
			}			
		});
    };
    
  //This function will  filters the Client wise report data by using the date
    $scope.getUserTypeTrackingReportDataByDate = function(userType){
    	var fromDateObj = $('#fromdatetime').data("DateTimePicker").getDate();
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	if( fromDateObj >= toDateObj ) 
    	{
    	    $(".ui-error-message").show();
    	} else {
    		$(".ui-error-message").hide();
    		$scope.generateUserTypeTrackingReportData(userType);
    	}
    };
    
    $scope.generateUserTypeTrackingReportData = function(userType) {
    	$scope.disable_submit = true;
		$scope.initializeDateValues();
	
		var url = 'web/report/userTrackingReport?fromDate='+$scope.fromDate +'&toDate='+$scope.toDate+'&userType='+userType;
		
		$http.get(url).then(function(response) {			
			if (response.data.status == 'ERROR') {
				$scope.disable_submit = false;
				alert('No Data is available with the given date range !!');
				return;
			} else {
				$scope.downloadReports(url);
			}			
		});
    };
    
    $scope.loadMiscReportData = function() {
    	$scope.disable_submit = true;
		$scope.initializeDateValues();
		var url = 'web/report/miscReport?fromDate='+$scope.fromDate +'&toDate='+$scope.toDate;
		$http.get(url).then(function(response) {			
			if (response.data.status == 'ERROR') {
				$scope.disable_submit = false;
				alert('No Data is available with the given date range !!');
				return;
			} else {
				$scope.downloadReports(url);
			}			
		});
		$scope.disable_submit = false;
    };
    
    
    $scope.loadCodersQualityReport = function(userType) {
    	$scope.disable_submit = true;
    	$scope.initializeDateValues();
    	
    	var url = 'web/report/coderwiseQualityReport?fromDate='+$scope.fromDate +'&toDate='+$scope.toDate+'&userType='+userType;
		
		$http.get(url).then(function(response) {			
			if (response.data.status == 'ERROR') {
				$scope.disable_submit = false;
				alert('No Data is available with the given date range !!');
				return;
			} else {
				$scope.downloadReports(url);
			}			
		});
    };
    
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    $scope.reportItems = [];
    
    $scope.loadCoderQAsCNRChartCount = function(userType) {
    	$scope.disable_submit = true;
    	$scope.initializeDateValues();
		
    	var url = "";
    	if(userType == 'coder') {
    		url = "web/report/userCNRChartsCount?userType=coder&fromDate="+$scope.fromDate +"&toDate="+$scope.toDate;
    	} else if(userType == 'qa') {
    		url = "web/report/userCNRChartsCount?userType=qa&fromDate="+$scope.fromDate +"&toDate="+$scope.toDate;
    	}
    	
		$http.get(url).success(function(response) {
			if (response.status == 'ERROR') {
				$scope.disable_submit = false;
				alert('No Data is available with the given date range !!');
				return;
			} else {
				$scope.disable_submit = false;
				$scope.reportItems = response.payLoad;
				
				// For paging
    			$scope.currentPage = 0;
				// now group by pages
    			$scope.groupToPages();
    			
    			//For pagination
				if( $scope.reportItems != null)
					$scope.bigTotalItems = $scope.reportItems.length;
				else
					$scope.bigCurrentPage = 0;
			}			
		}).error(function(response) {
           console.log('Could not Fetch Tasks'+ response.message);
        }); 
    }
    
    $scope.loadCoderQAsCNRReportDetail = function(data, role) {
    	if (data != undefined){
    		$rootScope.userData = data;
    		
    		if( role == 'coder') {
        		$location.path('/CoderCNRReportDetail');
        	} else if(role == 'qa') {
        		$location.path('/QACNRReportDetail');
        	}
    	} 
    }
    
    $scope.loadReportDetail = function() {
    	$scope.disable_submit = true;
    	if ( $rootScope.userData ) {
    		
    		$http.post('web/report/userCNRReportDetails',  $rootScope.userData).success(function(response) {			
    			if (response.status == 'ERROR') {
    				$scope.disable_submit = true;
    				alert('No Data is available for selected user with the given date range !!');
    				return;
    			} else {
    				$scope.reportItems = [];
    				console.log(" response.payLoad  :: ==> " + response.payLoad  );
    				$scope.reportItems = response.payLoad;
    				
    				// For paging
        			$scope.currentPage = 0;
    				// now group by pages
        			$scope.groupToPages();
        			
        			//For pagination
    				if( $scope.reportItems != null) {
    					$scope.bigTotalItems = $scope.reportItems.length;
    				} else {
    					$scope.bigCurrentPage = 0;
    				}
    			}			
    		});
    	} else {
    		
    	}
    }
    
    $scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
	};
		
    // calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		if($scope.reportItems && $scope.reportItems.length > 0) {
			for (var i = 0; i < $scope.reportItems.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.reportItems[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.reportItems[i]);
				}
			}
		}
		
	};
	
	//Initialization function. 
    $scope.init = function(){
    	$scope.loadAllClients();
    };
    
    $scope.init();
    
    // this function used to open the popup window.
    $scope.openPopup = function (message) {
	    var modalInstance = $modal.open({
	      templateUrl: 'myModalContent.html',
	      controller: ModalInstanceCtrl,
	      resolve: {
	    	  msg: function () {
	    		  return message;
	    	  }
	      }
	    });
    };
    
};

//$modalInstance represents a modal window (instance) dependency.
//It is not the same as the $modal service used above.
var ModalInstanceCtrl = function ($scope, $modalInstance, msg) {

	$scope.msg = msg;
	
	$scope.ok = function () {
	 $modalInstance.close('ok');
	};
	
	$scope.cancel = function () {
	 $modalInstance.dismiss('cancel');
	};

};
