'use strict';

/**
 * CoderController
 * @constructor
 */
var CoderAllTasksController = function($scope, $http, $location, $modal, $rootScope, $routeParams, $filter, 
		SearchService, CoderFormService, SortService) {
	$scope.$location = $location;
	$scope.userId = null;
	$scope.groupedItems = [];
	$scope.filteredItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.alerts = [];
	$scope.worklists = [];
	$scope.showAlert = null; 
	$scope.clients = [];
	$scope.chartTypes = [];
	$scope.dateTime = '';
	$scope.states = [];
	
	var dt = new Date();
	dt.setDate(dt.getDate() - 1);
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
	
    $scope.loadCoderWorkedItems = function() {
		$scope.showAlert = false;
		$scope.alerts = [];
		$scope.queryString = {};
		$scope.queryString.userId = $rootScope.user.userId;
		$scope.queryString.fromDate = $scope.fromDate;
		$scope.queryString.toDate = $scope.toDate;
    	$http.post('web/coder/coderWorkedItems', $scope.queryString).success(function(response, status) {
			$scope.worklists = response.payLoad;
			$scope.message = response.message;
			if (response.status == 'ERROR') {
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;				
			} else {
				$scope.alerts.push({msg: response.message});
				$scope.showAlert = true;				
			}			
			$scope.search();
		}).error(function(response) {
           console.log('Could not Fetch Tasks'+ response.message);
        }); 
    };
    
    $scope.fetchCoderWorkItemByDate = function() {
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
        		$scope.validateDate = true;
        		$scope.fromDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
        		$scope.toDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
        		
        		$scope.loadCoderWorkedItems();
        	}
    		$scope.validateDate = true;
    	}
	};
   
	
	$scope.initializeUserDetails = function () {
    	var toDateObj = $('#datetimepicker').data("DateTimePicker").getDate();
    	if(toDateObj) {
    		$scope.dateTime = toDateObj.year() +"-" + (toDateObj.month()+1) + "-" + toDateObj.date();
    		$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    		 $scope.loadCoderWorkedItems();
    	}
    };
    
    $scope.editTask =  function ( taskId ) {
		  $scope.showAlert = false;
		  alert(taskId);
    };
  
	
	/**
	 * Soring the column data
	 */
	// change sorting order
    $scope.sort_by = function(newSortingOrder) {
        if ($scope.sortingOrder == newSortingOrder){
            $scope.reverse = !$scope.reverse;
        }
        $scope.sortingOrder = newSortingOrder;
        if ($scope.reverse) {
        	$scope.filteredItems =  SortService.sortByKeyDesc($scope.filteredItems, newSortingOrder);
        } else {
        	$scope.filteredItems =  SortService.sortByKeyAsc($scope.filteredItems, newSortingOrder);
        }
    	$scope.groupToPages();
    };
    
    // calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		if($scope.filteredItems && $scope.filteredItems.length > 0){
			for (var i = 0; i < $scope.filteredItems.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
				}
			}
		}
	};
	
	$scope.search = function() {
	
		$scope.searchItems = SearchService.search($scope.worklists,  $scope.query);
		
		//Filter data by client
		if($scope.queryClient && $scope.queryClient.length > 0) {
			$scope.searchItems = SearchService.searchByClient($scope.searchItems, $scope.queryClient);
		}

		//Filter data by chartType
		if($scope.queryChartType && $scope.queryChartType.length > 0) {
			$scope.searchItems = SearchService.searchByChartType($scope.searchItems, $scope.queryChartType);
		}
		
		//Filter data by status
		if($scope.queryStatusType && $scope.queryStatusType.length > 0) {
			$scope.searchItems = SearchService.searchByStatusType($scope.searchItems, $scope.queryStatusType);
		}
		$scope.filteredItems =  $scope.searchItems;
		if($scope.currentPage > 0){
			$scope.currentPage = $scope.currentPage;
		} else {
			$scope.currentPage = 0;
		}
		// now group by pages
		$scope.groupToPages();
		
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
		
		$("#displayChartHistory").hide();
		$("#chartHistoryTable").hide();
	};
	

	$scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
	};

	$scope.loadAllClients = function() {
		$http.get('web/client/detail/all').success(function(data) {
			$scope.clients = data.payLoad;
		});
	};
	
	
	$scope.loadAllChartTypes = function() {
		$http.get('web/client/detail/allChartTypes').success(function(data) {
			$scope.chartTypes = data.payLoad;
		});
	};

	$scope.loadAllStates = function() {
		$http.get('web/coder/states').success(function(data) {
			$scope.states = data.payLoad;
		});
	};

	 // this function used to open the table to display User Level Coding Data History.
	 $scope.viewUserLevelCodDiffHistory = function(codingData, chartType, status) {
		 $scope.chartHistory = [];
		 $scope.chartItems = [];
		 if(codingData && codingData.length > 0) {
			 for(var i=0; i<codingData.length; i++) {
				 $scope.chartHistory = JSON.parse(codingData[i]);
				 $scope.chartItems.push($scope.chartHistory);
				 
				 if($scope.chartItems.length == codingData.length) {
					 var tempURL = '';
					 if(chartType == "IP") {
						 tempURL = 'app/coder/views/coding_diff_popup_ip.html';
					 } else if(chartType == "GE") {
						 tempURL = 'app/coder/views/coding_diff_popup_ge.html';
					 } else if(chartType == "EKG_ECG" || chartType == "EKG") {
						 tempURL = 'app/coder/views/coding_diff_popup_ekg_ecg.html';
					 } else {
						 tempURL = 'app/coder/views/coding_diff_popup.html';
					 }
					 $scope.openPopup($scope.chartItems, tempURL);
				 }
			 }
			
			 $("#displayChartHistory").show();
			 $("#chartHistoryTable").show();
			 
			 
		 } else {
			 $("#chartHistoryTable").hide();
			 $("#displayChartHistory").hide();
		 }
	 };
	  
	 
	 $scope.init = function () {
		//Initialized the filters data
		$scope.loadAllChartTypes();
		$scope.loadAllClients();
		$scope.loadAllStates();
	};
		
	$scope.init();
		
	 // this function used to open the popup window.
    $scope.openPopup = function (data, tempURL) {
	    var modalInstance = $modal.open({
	      templateUrl: tempURL,
	      controller: ModalInstanceCtrl,
	      resolve: {
	        msg: function () {
	          return data;
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
