'use strict';

/**
 * DeleteWorklistDataController
 * 
 * @param $scope
 * @param $http
 * @param $filter
 * @param $routeParams
 * @param $location
 * @param $modal
 * @param SearchService
 */
function DeleteWorklistDataController($scope, $rootScope, $http, $filter, $routeParams, 
		$location, $modal, $route, SearchService, DataService, SortService) {

    $scope.$location = $location;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5; //Do not change this value.If change here then change in "ui-bootstrap-tpls-0.10.0.js" also.
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.searchItems = [];
	$scope.selectDate = "";
	
	$(function () {
		$('#datetimepicker').datetimepicker({
			  pickTime: false,
			 // minDate: dt,  
			  maxDate: new Date(), 
			  showToday: true
		}).on("dp.change", function(e){
	        $scope.setupDate();
	    });
	});
    
    $scope.loadProgressBar = function(){
	 	$('#content').waitMe({
	 		effect : 'rotation',
	 		text : 'Please wait...',
	 		bg: 'rgba(255,255,255,0.7)',
	 		color : '#000',
	 		sizeW : '',
	 		sizeH : ''
	 	});
	}
    
    $scope.setupDate = function() {
		var dateTimeObj = $('#datetimepicker').data("DateTimePicker").getDate();
		if(dateTimeObj != null) {
			$scope.dateTime = dateTimeObj.year() +"," + dateTimeObj.month() + "," + dateTimeObj.date();
			$scope.selectDate = $filter('date')(new Date(dateTimeObj), 'MM/dd/yyyy');
			$scope.loadWorklistDataByDate();
		}
	};
	
    /* functions has been used to search the data
     * setup the data to display
     */
    $scope.search = function() {
		$scope.searchItems = SearchService.search($scope.items,  $scope.query);
		//Filter data by client
		if($scope.queryClient && $scope.queryClient.length > 0) {
			$scope.searchItems = SearchService.searchByClient($scope.searchItems, $scope.queryClient);
		}
		//Filter data by chartType
		if($scope.queryChartType && $scope.queryChartType.length > 0) {
			$scope.searchItems = SearchService.searchByChartType($scope.searchItems, $scope.queryChartType);
			$scope.loadspecialization();
		}
		//Filter data by chartSpecialization
		if($scope.queryChartSpl && $scope.queryChartSpl.length > 0) {
			$scope.searchItems = SearchService.searchByChartSpecialization($scope.searchItems, $scope.queryChartSpl);
		}
		//Filter data by status
		if($scope.queryStatus && $scope.queryStatus.length > 0) {
			$scope.searchItems = SearchService.searchByStatus($scope.searchItems, $scope.queryStatus);
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
			$scope.bigCurrentPage = 0;
		}
	};

	$scope.setPage = function () {
	  $scope.currentPage = $scope.bigCurrentPage - 1;
	};
		
	$scope.loadWorklistDataByDate = function() {
		$scope.loadProgressBar();
		$http.get('web/supervisor/allTaskDetails?date='+$scope.dateTime)
		.success(function(response) {
			if (response.status == "OK") {
				$scope.items = response.payLoad;
				
				$scope.search();
			} else {
				$scope.messageAlert = response.message;
			}
			$('#content').waitMe('hide');
		}).error(function(response) {
           console.log('ERROR ==> Could not get worklist Data : '+ response.message);
           $('#content').waitMe('hide');
        }); 
    };
    
	// calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		if($scope.filteredItems && $scope.filteredItems.length > 0) {
			for (var i = 0; i < $scope.filteredItems.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
				}
			}
		}
	};
	
	
	//Load All clients details to use in filters
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
	$scope.loadChartSpecialization = function() {
		$http.get('web/supervisor/getAllUniqueChartSpecialization').success(function(data){
			$scope.chartSpls = data.payLoad;
		});
	};
	$scope.loadAllChartStatus = function() {
		/* Loads the clientconfig.properties files and assign the data to field */
		$http.get('app/clientconfig.properties').then(function (response) {
			console.log("response.data : " +response.data)
			if(response.data)
			{
				$scope.chartsStatus = response.data.monitorTaskChartStatus;
			} 
			
		});
	};
	$scope.loadspecialization = function(){
		$scope.chartSpls = [];
		if($scope.queryChartType){
			var url = 'web/supervisor/getChartTypeSpecialization/'+$scope.queryChartType;
	        $http.get(url).success(function(data) {
	        	$scope.chartSpls = data.payLoad;
	            return;
	        });
		} else {
			$scope.loadChartSpecialization();
		}
    }
	
    $scope.checkAll = function () {
        if ($scope.selectedAll) {
            $scope.selectedAll = true;
        } else {
            $scope.selectedAll = false;
        }
        angular.forEach($scope.pagedItems, function (items) {
        	angular.forEach(items, function (item) {
               item.Selected = $scope.selectedAll;
            });
        });
    };

    $scope.deleteChart = function(){
    	$scope.selectedIds = [];
    	 angular.forEach($scope.pagedItems, function (items) {
         	angular.forEach(items, function (item) {
                if(item.Selected == true){
                	$scope.selectedIds.push(item.id);
                }
             });
         });
    	 $scope.queryString = {"taskIds":$scope.selectedIds};
    	 if($scope.selectedIds.length > 0) {
    		 $http.post("web/supervisor/deleteWorklist", $scope.queryString).success(function(response){
        		 if(response.status == 'OK') {
        			 $route.reload();
        			 alert("Selected Items has been deleted !")
        			 return;
        		 } else {
        			 alert("Unalbe to delete selected Item, please try to delete one by one !")
        			 return
        		 }
        	 }).error(function(response) {
                 console.log('Could not Delete worklist items '+ response.message);
             });
    	 } else {
    		 alert("Select chart to delete !")
    		 return;
    	 }
    	 
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
    
    
	$scope.init = function () {
		console.log("Initilized.........." +DataService)
		//Initialized the filters data
		$scope.loadAllChartStatus();
		$scope.loadAllClients();
		$scope.loadAllChartTypes();
		$scope.loadChartSpecialization();
		
		$scope.setupDate();
    };
	
   
    
	//Initialization of the controller by calling the init method.
    //$scope.init();
    
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

