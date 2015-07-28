'use strict';

/**
 * AuditGradingController
 * @constructor
 */
var AuditGradingController = function($scope, $http, $location, $rootScope, $routeParams, $filter, $modal,
		CoderFormService, LinkHandlerService, SearchService, ngDialog) {
	
	$scope.$location = $location;
	$scope.userId = null;
	$scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.filteredItems = [];
    $scope.currentPage = 0;
	$scope.alerts = [];
	$scope.auditWorkItems = [];
	$scope.showAlert = null;
	$scope.messageAlert = [];
	$scope.disableFetch = false;
	$scope.showRequired = false;
	$scope.showAlert = null;	
	
	$scope.gradSheet = {};
	$scope.gradSheetData = [];
	$scope.weightage = {};
	$scope.clientDetails = [];
	$scope.coderDetails = [];
	$scope.queryCoder = null;
	$scope.globalAudit = false;
	$scope.localAudit = false;
	
	var dt = new Date();
	dt.setDate(dt.getDate() - 30);
	$(function () {
		$('#fromdatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date(),
		});
		$('#todatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date(),
		});
	});
	
	$scope.loadProgressBar = function(){
	 	$('#box-content').waitMe({
	 		effect : 'rotation',
	 		text : 'Please wait...',
	 		bg: 'rgba(255,255,255,0.7)',
	 		color : '#000',
	 		sizeW : '',
	 		sizeH : ''
	 	});
	}
	
	/* Load the Audit Task Details*/
    $scope.fetchTaskTOAudit = function () {
    	$scope.requestData = {};
    	$scope.requestData.userId = $rootScope.user.userId;
    	$scope.requestData.coderId = $scope.coderId;
    	$scope.requestData.clientId = $scope.queryClient;
    	$http.post('web/coder/fetchAuditChart', $scope.requestData).success(function(response, status) {
         	if(response.status == 'OK' && response.payLoad != null) {
         		if( response.payLoad != null) {
         			$scope.message = response.message;
         			$scope.auditWorkItems.push(response.payLoad); 
         		} else {
     				$scope.messageAlert = response.message; 
     				$scope.showAlert = true;
     			}
         	} else {
         		$scope.messageAlert = response.message;
 				$scope.showAlert = true;	
 			} 
         	
         	if($scope.showAlert) {
         		$scope.openPopup($scope.messageAlert);
         	} else {
     			// now group by pages
     			$scope.groupToPages();
         	}
 		}).error(function(response) {
 			console.log("Error Occurred");
             //$scope.setError('Could not Fetch Task'+response.getMessage());
        }); 
     };
     
     $scope.fetchActiveAuditTasks = function() {
     	$http.post('web/coder/fetchActiveAuditCharts', $rootScope.user.userId).success(function(response, status) {
          	if(response.status == 'OK' && response.payLoad != null) {
  				$scope.messageAlert = response.message;
      			if (response.payLoad.length > 0) {
      				$scope.auditWorkItems = response.payLoad;
      			}
      			// now group by pages
      			$scope.groupToPages();
          	} 
  		}).error(function(response) {
  			console.log("Error Occurred");
              //$scope.setError('Could not Fetch Task'+response.getMessage());
         }); 
     };
     
     $scope.loadGradingsheetForm = function(taskId, chartType) {
    	 $scope.$parent.selectedTaskID = taskId;
    	 $scope.$parent.selectedTaskChartType = chartType;
		 $scope.$parent.audit = true;
		 $scope.$parent.fromDate = $scope.startDate;
		 $scope.$parent.toDate = $scope.endDate;
		 //disable all coder/qa menubar links.
		 LinkHandlerService.disableCoderQAMenubarLinks();
		 $location.path('/audit_chart/'+chartType.toLowerCase()+'/'+taskId+'/'+true);
     };
     
     $scope.loadAllClients = function() {
		$http.get('web/client/detail/allClientsDetail').success(function(data) {
			$scope.clientDetails = data.payLoad;
		});
	 };
	
	$scope.loadAllChartTypes = function() {
		$http.get('web/client/detail/allChartTypes').success(function(data) {
			$scope.chartTypes = data.payLoad;
		});
	};
	
	$scope.loadCodersByClient = function() {
		$http.get('web/coder/getCodersDetailByClient?clientId='+ parseInt($scope.queryClient) + '&auditorId='+$scope.userId).success(function(response) {
			if(response.status == "OK") {
				$scope.coderDetails = response.payLoad;
				if($scope.coderDetails){
					$("#coderDetailDiv").show();
				}
			} else {
				$("#coderDetailDiv").hide();
				alert("No coder found for selected Client!!!!")
			}
		});
	};
	
     // calculate page in place
     $scope.groupToPages = function () {
	   	 $scope.pagedItems = [];
	   	 if ( $scope.auditWorkItems && $scope.auditWorkItems.length > 0 ) {
				for (var i = 0; i < $scope.auditWorkItems.length; i++) {
					if (i % $scope.itemsPerPage === 0) {
						$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.auditWorkItems[i] ];
					} else {
						$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.auditWorkItems[i]);
					}
				}
	   	 }
	 };
	 
	 /**
	  * This function loads
	  * All the charts which are completed by local qa
	  * For audit, Remote Qa will do audit for these charts 
	  */
	 $scope.loadDailyAuditTasks = function() {
		 $scope.loadProgressBar();
	 	$(".ui-error-message").hide();
	 	var fromDateObj = 0;
	 	var toDateObj = 0;
    	var fromDateObj = $('#fromdatetime').data("DateTimePicker").getDate();
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	
    	if($scope.$parent.fromDate && $scope.$parent.toDate){
    		$scope.startDate = $scope.$parent.fromDate;
			$scope.endDate = $scope.$parent.toDate;
    	} else {
    		$scope.startDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
			$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    	}
    	
    	
		$scope.requestData = {};
     	$scope.requestData.userId = $rootScope.user.userId;
     	$scope.requestData.startDate = $scope.startDate;
     	$scope.requestData.endDate = $scope.endDate;
     	$http.post('web/coder/fetchGlobalQAAuditItems', $scope.requestData).success(function(response) {
          	if(response.status == 'OK' && response.payLoad != null) {
      			if (response.payLoad.length > 0) {
      				$scope.auditWorkItems = response.payLoad;
      				$scope.search();
      			}
      			$scope.messageAlert = response.message;
      			$scope.showWarnMessage = false;
          	} else if(response.status == 'VALIDATION_ERROR' && response.statusCode == 412){
          		$(".ui-error-message").show();
          	 	$('#box-content').waitMe('hide');
        	    return false;
          	} else{
          		$scope.auditWorkItems = [];
          		$scope.search();
  				$scope.showWarnMessage = true;	
          	}
          	$('#box-content').waitMe('hide');
  		}).error(function(response) {
  			console.log("Error Occurred");
  			$('#box-content').waitMe('hide');
        }); 
	};
		
	// calculate page in place
     $scope.groupToAuditPages = function () {
	   	 $scope.auditItems = [];
	   	 if ( $scope.filteredItems && $scope.filteredItems.length > 0 ) {
			for (var i = 0; i < $scope.filteredItems.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.auditItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
				} else {
					$scope.auditItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
				}
			}
	   	 }
	 };
	 
	 $scope.search = function() {
		$scope.searchItems = SearchService.search($scope.auditWorkItems,  $scope.query);
		//Filter data by client
		if($scope.queryClient && $scope.queryClient.length > 0) {
			$scope.searchItems = SearchService.searchByClient($scope.searchItems, $scope.queryClient);
		}
		//Filter data by chartType
		if($scope.queryChartType && $scope.queryChartType.length > 0) {
			$scope.searchItems = SearchService.searchByChartType($scope.searchItems, $scope.queryChartType);
		}
		//Filter data by DRG
		if($scope.queryDRG && $scope.queryDRG.length > 0) {
			$scope.searchItems = SearchService.searchByDRG($scope.searchItems, $scope.queryDRG);
		}
		
		$scope.filteredItems =  $scope.searchItems;
		if($scope.currentPage > 0){
			$scope.currentPage = $scope.currentPage;
		} else {
			$scope.currentPage = 0;
		}
		// now group by pages
		$scope.groupToAuditPages();
		
		//For pagination
		if($scope.filteredItems && $scope.filteredItems.length > 0){
			$scope.maxSize = 3;
			$scope.bigTotalItems = $scope.filteredItems.length;
			$scope.bigCurrentPage = 0;
			$scope.currentPage = 0;
		} else {
			$scope.maxSize = 0;
			$scope.currentPage = 0;
		}
		/*pagination code done */
	};
		

	$scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
	};
	
    // Open the popup window to display error message.
	$scope.openErrorMessage = function () {
		var dialog = ngDialog.open({
			template: '<p>Other user is working on this, Please select other task.</p>',
			className: 'ngdialog-error-theme-plain',
			plain: true
		});
		dialog.closePromise.then(function (data) {
			$scope.searchQuery='';
			$scope.disableSearch = false;
		});
		$scope.disableSearch = true;
	};
		
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
     
    // init method to initialized the from on load.
    $scope.init = function () {
    	$("#coderDetailDiv").hide();
		$http.get('web/user/userdetails').success(function(response) {
    		if (response.status == "OK") {
    			$rootScope.user = response.payLoad;
    			if($rootScope.user) {
    				$scope.loadAllClients();
    				$scope.loadAllChartTypes();
    				if($rootScope.user.userRole == 'RemoteQA') {
    					$scope.globalAudit = true;
    					$scope.localAudit = false;
    					$scope.loadDailyAuditTasks();
    				} else {
    					$scope.globalAudit = false;
    					$scope.localAudit = true;
    					$scope.fetchActiveAuditTasks();
    				}
    			}
    		} else {
    			$location.path("/login");
    		}
    	});
    }
    
    //Initialized the logged-in user details and other required details 
    $scope.init();   
    
};

// $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.
var ModalInstanceCtrl = function ($scope, $modalInstance, msg) {

  $scope.msg = msg;
 
  $scope.ok = function () {
    $modalInstance.close('ok');
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
  
};