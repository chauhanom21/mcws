'use strict';

/**
 * SupervisorReportsController
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
function SupervisorReportsController($scope, $rootScope, $http, $filter, $routeParams, $location, $modal, Restangular, SearchService) {

	var dt = new Date();
	dt.setDate(dt.getDate() - 30);
	$(function () {
		$('#fromdatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: dt
	//		  showToday: false
		});
		
		$('#todatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date()
			  //showToday: true
		});
	});

	

    $scope.$location = $location;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5; //Do not change this value.If change here then change in "ui-bootstrap-tpls-0.10.0.js" also.
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.searchItems = [];
	$scope.clients = [];
	$scope.clientIds = [];
	$scope.chartTypes = [];
	$scope.fromDate = [];
    $scope.toDate = [];
    $scope.startDate = "";
    $scope.endDate = "";
    
    //Gets the date value from date fields and assign to variable.
    $scope.initializeDateValues  = function() {
    	var fromDateObj = 0;
    	if($('#fromdatetime').data("DateTimePicker")){
    		fromDateObj = $('#fromdatetime').data("DateTimePicker").getDate();
    	}
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	
    	if(fromDateObj && toDateObj)
    	{
    		$scope.fromDate = fromDateObj.year() +"-" + (fromDateObj.month()+1) + "-" + fromDateObj.date();
    		$scope.toDate = toDateObj.year() +"-" + (toDateObj.month()+1) + "-" + toDateObj.date();
    		
    		$scope.startDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
    		$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    	}
    };
    
	$scope.loadAllSupervisorReportsData = function() {
		
		$scope.initializeDateValues();
		
		$http.get('web/supervisor/getSupervisorReportsData?fromDate='+$scope.fromDate+'&toDate='+$scope.toDate).success(function(response, status) {
			if (response.status == 'ERROR') {
				$scope.messageAlert = response.message;
			} else {
				$scope.message = response.message;
				$scope.items = response.payLoad;
				
			}
			
			// functions have been describe process the data for display
			$scope.search();
		
			//Function have been initialized 
			$scope.loadAllClients();
			$scope.loadAllClientIds(); 
			$scope.loadAllChartTypes();
			
		}).error(function(response) {
           console.log('Could not get reports Data : '+ response.message);
        }); 
    };
    
	$scope.search = function() {
		
		$scope.searchItems = SearchService.search($scope.items,  $scope.query);
	
		//Filter data by client
		if($scope.queryClient && $scope.queryClient.length > 0) {
			$scope.searchItems = SearchService.searchByClient($scope.searchItems, $scope.queryClient);
		}
		//Filter data by status
		if($scope.queryChartType && $scope.queryChartType.length > 0) {
			$scope.searchItems = SearchService.searchByChartType($scope.searchItems, $scope.queryChartType);
		}
		$scope.filteredItems =  $scope.searchItems;
		// take care of the sorting order
		if ($scope.sortingOrder !== '') {
			$scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
		}
		$scope.currentPage = 0;
		// now group by pages
		$scope.groupToPages();

		/* For pagination */
		if($scope.filteredItems.length > 0){
			$scope.maxSize = 3;
			$scope.bigTotalItems = $scope.filteredItems.length;
			$scope.bigCurrentPage = 0;
		} else {
			$scope.maxSize = 0;
			$scope.bigTotalItems = $scope.filteredItems.length;
			$scope.bigCurrentPage = 0;
		}
		/*pagination code done */
	};

	$scope.setPage = function () {
	  $scope.currentPage = $scope.bigCurrentPage - 1;
	};
			
	// calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		if($scope.filteredItems.length > 0) {
			for (var i = 0; i < $scope.filteredItems.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
				}
			}
		}
	};
	
	$scope.loadAllClients = function() {
		$http.get('web/client/detail/all').success(function(data) {
			$scope.clients = data.payLoad;
		});
	};
	
	//clientIds
	$scope.loadAllClientIds = function() {
		$http.get('app/clientconfig.properties').then(function (response) {
			if(response.data) {
				$scope.clientIds = response.data.clientIds;
			} 
		});
	};
	
	$scope.loadAllChartTypes = function() {
		$http.get('web/client/detail/allChartTypes').success(function(data) {
			$scope.chartTypes = data.payLoad;
		});
	};
	
	$scope.init = function () {
		$scope.loadAllSupervisorReportsData();
    };
	
	//Initialization of the controller by calling the init method.
    $scope.init();
    
    
    $scope.downloadReport = function(type) {
    	if(!$scope.filteredItems || $scope.filteredItems.length <= 0) {
    		$scope.msg = "Do not have data to download. Please change the Filters/Date Field Value!";
			$scope.openPopup($scope.msg);
    	} else {
	    	$scope.initializeDateValues();
	    	var client = "";
	    	var chartType = "";
	    	if($scope.queryClient && $scope.queryClient.length > 0){
	    		client = $scope.queryClient;
	    	}
	    	if($scope.queryChartType && $scope.queryChartType.length > 0){
	    		chartType = $scope.queryChartType;
	    	}
			var url = 'web/report/download?type='+type +'&fromDate='+$scope.fromDate +'&toDate='+$scope.toDate +"&client="+client +"&chartType="+chartType;
			
			// Start download
			 window.open(url, '_blank');
			 window.focus();
    	}
    
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