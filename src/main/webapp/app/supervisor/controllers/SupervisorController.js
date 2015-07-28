'use strict';
var sortingOrder = 'name';
function MonitorTaskController($scope, $rootScope, $http, $filter, $routeParams, $location, 
		Restangular, SearchService, SortService) {
    $scope.$location = $location;
    $scope.filteredItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.items = [];
	$scope.dateTime = [];
	$scope.status = [];
	$scope.endDate = "";
	$scope.sortingOrder  = sortingOrder;
    $scope.reverse       = false;
	$scope.showCodersList = false;
	
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
	
	var dt = new Date();
	dt.setDate(dt.getDate() - 30);
	$(function () {
		$('#datetimepicker').datetimepicker({
			  pickTime: false,
			  minDate: dt,  
			  maxDate: new Date(), 
			  showToday: true
		}).on("dp.change", function(e){
	        $scope.fetchTaskDataByDate();
	    });
	});
	
	// calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		for (var i = 0; i < $scope.filteredItems.length; i++) {
			if (i % $scope.itemsPerPage === 0) {
				$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
			} else {
				$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
			}
		}
	};

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
		if($scope.filteredItems.length > 0){
			$scope.maxSize = 3;
			$scope.bigTotalItems = $scope.filteredItems.length;
			$scope.bigCurrentPage = 0;
			$scope.currentPage = 0;
		} else {
			$scope.maxSize = 0;
			$scope.currentPage = 0;
			$scope.bigCurrentPage = 0;
		}
		
		//Display coder/qa list
		$scope.loadCodersDetails($scope.queryClient, $scope.queryChartType, $scope.queryChartSpl, $scope.queryStatus);
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

	$scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
	};
		
	$scope.remoteQA = [];
	$scope.localQA = [];
	$scope.coders = [];
	$scope.codersList = [];
	$scope.chartsStatus = [];
	$scope.clients = [];	
	
    $scope.loadRemoteQA = function(status, clientId, chartSpecId) {
    	console.log("loadRemoteQA --->  status : " + status + " & clientId :" + clientId + " & chartSpecId :" + chartSpecId);
    	$scope.remoteQA = [];
    	if(status != null && (status == $scope.status.GlobalCNR || status == $scope.status.GlobalQAAssigned || status == $scope.status.InComplete )) 
    	{
    		var url = 'web/user/detail/remoteQA/'+clientId+"/"+chartSpecId;
    		return $scope.remoteQA.length ? null : $http.get(url).success(function(data) {
                $scope.remoteQA = data.payLoad;
            });
    		
    	} 
    	else 
    	{
    		return $scope.remoteQA;
    	}
    };
	
	$scope.loadLocalQA = function(status, clientId, chartSpecId) {
		console.log("loadLocalQA --->  status : " + status + " & clientId :" + clientId + " & chartSpecId :" + chartSpecId);
		$scope.localQA = [];
		if(status != null && ( status == $scope.status.LocalCNR || status == $scope.status.LocalQAAssigned ||status == $scope.status.CompletedNR
				 || status == $scope.status.InComplete)) 
		{
			var url = 'web/user/detail/localQA/'+clientId+"/"+chartSpecId;
			return $scope.localQA.length ? null : $http.get(url).success(function(data) {
	            $scope.localQA = data.payLoad;
	        }); 
			 
		} 
		else 
		{
			return $scope.localQA;
		}
	};
	
	$scope.loadCoders = function(status, clientId, chartSpecId) {
		console.log("loadCoders ---->  status : " + status + " & clientId :" + clientId + " & chartSpecId :" + chartSpecId);
		$scope.coders = [];
		if(status != null && (status == $scope.status.InComplete || status == $scope.status.NotStarted || status ==  $scope.status.CoderAssigned )) 
		{
			var url = 'web/user/detail/coders/'+clientId+"/"+chartSpecId;
			return $scope.coders.length ? null : $http.get(url).success(function(data) {
				$scope.coders = data.payLoad;
			}); 
		} 
		else 
		{
			 return $scope.coders;
		}
	};
	
	/* Send post request to SupervisorController to update the task details.*/
    $scope.updateTask = function(data, id, reviewWorkListId, status) {
		angular.extend(data, {id: id, reviewWorkListId:reviewWorkListId, status:status});
		
        $http.post('web/supervisor/updateTask', data).success(function(data){
        	// functions have been reload all data again
        	$scope.loadTaskDataByDate();
        	return;
        });
    };
	
		
	$scope.showRemoteQA = function(task) {
	    if(task.remoteQAId && $scope.remoteQA.length) {
	        var selected = $filter('filter')($scope.remoteQA, {id: task.remoteQAId});
            return selected.length ? selected[0].name : 'Not set';
        } else {
            return task.remoteQAName || 'Not set';
        }
    };
	
	$scope.showLocalQA = function(task) {
        if(task.localQAId && $scope.localQA.length) {
		    var selected = $filter('filter')($scope.localQA, {id: task.localQAId});
            return selected.length ? selected[0].name : 'Not set';
        } else {
            return task.localQAName || 'Not set';
        }
    };
	
	$scope.showCoders = function(task) {
        if(task.coderId && $scope.coders.length) {
			var selected = $filter('filter')($scope.coders, {id: task.coderId});
            return selected.length ? selected[0].name : 'Not set';
        } else {
            return task.coderName || 'Not set';
        }
    };
	
    //Load coders belongs to Filters data
    $scope.loadCodersDetails = function(clientName, chartType, chartSpls, status) {
    	$scope.showCodersList = false;
		if(clientName && chartType && chartSpls && status) {
			$scope.codersList = [];
			if($scope.filteredItems.length > 0 && status != null 
					&& (status == $scope.status.NotStarted || status ==  $scope.status.LocalCNR )) 
			{
				var url = 'web/user/detail/coders/'+clientName+"/"+chartType+"/"+chartSpls+"/"+status;
				$http.get(url).success(function(data) {
					if(data.payLoad && data.payLoad != null && data.payLoad.length > 0){
						$scope.codersList = data.payLoad;
						$scope.showCodersList = true;
					}
				}); 
			} 
		}
		else 
		{
			 return $scope.coders;
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
			if(response.data) {
				$scope.chartsStatus = response.data.monitorTaskChartStatus;
			} 
		});
	};
	
	
	$scope.fetchTaskDataByDate = function() {
		var dateTimeObj = $('#datetimepicker').data("DateTimePicker").getDate();
		if(dateTimeObj != null) {
			console.log(dateTimeObj.toString());
			//cal.set(year, month, date)
			$scope.dateTime = dateTimeObj.year() +"," + dateTimeObj.month() + "," + dateTimeObj.date();
			$scope.endDate = $filter('date')(new Date(dateTimeObj), 'MM/dd/yyyy');
			$scope.loadTaskDataByDate();
		}
	};
	
	// function loads All Task Data
	$scope.loadTaskDataByDate = function () {
		$scope.loadProgressBar();
		$http.get("web/supervisor/allTaskDetails?date="+$scope.dateTime).success(function(obj) {
			if (obj.status == "OK"){
				var data = obj.payLoad;
				$scope.items = data;
				// functions have been describe process the data for display
				$scope.search();
				
			} else {
				console.log("warning", "No Data found!!");
			}
			$('#content').waitMe('hide');
		}, function(e) {
			alert('error :: ' +e);
			$('#content').waitMe('hide');
		});	
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
    
    $scope.chartIds = [];
    $scope.constSelectChartDetails = function(chartId){
    	$scope.chartIds = [];
   	 	angular.forEach($scope.pagedItems, function (items) {
        	angular.forEach(items, function (item) {
               if(item.Selected == true){
               	$scope.chartIds.push(item.id);
               }
            });
        });
   	 	console.log("Final Selected for DeleteInitilized.........." +$scope.chartIds)
    };
    
    $scope.chartSelectError = false;
    $scope.assignSelectedChartsToCoder = function(status) {
    	$scope.constSelectChartDetails();
    	$scope.chartSelectError = false;
    	if($scope.chartIds && $scope.chartIds.length > 0 && $scope.coder) {
    		$scope.queryString = {"coderId":$scope.coder, "status":$scope.queryStatus, "taskIds":$scope.chartIds};
            $http.post('web/supervisor/assignChartToCoder', $scope.queryString).success(function(response){
            	$scope.chartIds = [];
            	$scope.coder="";
            	// functions have been reload all data again
            	$scope.loadTaskDataByDate();
            	return;
            });
    	} else {
    		alert("Select chart to assign !");
    		return;
    	} 
    }
    
	// init function loads first in this controller
	$scope.init = function () {
		//Initialized the filters data
		$scope.loadAllChartStatus();
		$scope.loadAllClients();
		$scope.loadAllChartTypes();
		$scope.loadChartSpecialization();
		/* Loads the clientconfig.properties files and assign the data to configData field */
		$http.get('app/clientconfig.properties').then(function (response) {
			$scope.status = response.data.statusOptions;
		});
	};
		
	$scope.init();
};




/**
 * MonitorUserController
 * 
 * @param $scope
 * @param $rootScope
 * @param $http
 * @param $filter
 * @param $routeParams
 * @param $location
 * @param Restangular
 * @param SearchService
 * @param SortService
 */
function MonitorUserController($scope, $rootScope, $http, $filter, $routeParams, $location, Restangular, SearchService, SortService) {
    $scope.$location = $location;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.searchItems = [];
	$scope.usersName = [];
	$scope.userRoles = [];
	$scope.sortingOrder  = sortingOrder;
    $scope.reverse       = false;
    
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
    
	$scope.loadMonitorUsersDetails = function() {
		
		$scope.loadProgressBar();
		
	  $http.get("web/supervisor/allCodersAndQA").success(function(obj) {
			if (obj.payLoad != null && obj.payLoad.length == 0){
				console.log("warning", "No Coders/QA found!!");
				$('#content').waitMe('hide');
			} else {
				var data = obj.payLoad;
				
				$scope.items = data;
			
				$scope.setPage = function () {
					$scope.currentPage = $scope.bigCurrentPage - 1;
				};
				
				// functions have been describe process the data for display
				$scope.search();
				
				//Function have been initialized 
				$scope.loadAllUsersName();
				$scope.loadAllUsersRole();
				$scope.loadAllLocations();
		
				$('#content').waitMe('hide');
			}
		}, function(e) {
			$('#content').waitMe('hide');
			alert('err');
		});
		
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
			
	$scope.search = function() {
			
		$scope.searchItems = SearchService.search($scope.items,  $scope.query);
		
		//Filter the data by name
		if($scope.queryName && $scope.queryName.length > 0) {
			$scope.searchItems = SearchService.searchByName($scope.searchItems, $scope.queryName);
		}
		//Filter data by role
		if($scope.queryRole && $scope.queryRole.length > 0) {
			$scope.searchItems = SearchService.searchByRole($scope.searchItems, $scope.queryRole);
		}
		
		//Filter data by client
		if($scope.queryLocation && $scope.queryLocation.length > 0) {
			$scope.searchItems = SearchService.searchByLocation($scope.searchItems, $scope.queryLocation);
		}
		
		$scope.filteredItems =  $scope.searchItems;
		$scope.currentPage = 0;
		// now group by pages
		$scope.groupToPages();
		
		//For pagination
		if($scope.filteredItems) {
			if($scope.filteredItems.length > 0){
				$scope.maxSize = 3;
				$scope.bigTotalItems = $scope.filteredItems.length;
				$scope.bigCurrentPage = 0;
			} else {
				$scope.maxSize = 0;
				$scope.bigTotalItems = $scope.filteredItems.length;
				$scope.bigCurrentPage = 0;
			}
		}
	};
	
	/**
	 * Coder used for update coder availability
	 */
	$scope.enableEdit =  false;
	
	$scope.enableEditor = function(user){
		user.enableEdit = true;
	}
	
	$scope.disabledEditor = function(user) {
		console.log("After  Update USER Available : "+user.available)
		user.enableEdit = false;
	}
	
	$scope.updateUserDetail = function(user){
		$scope.loadProgressBar();
		console.log("Before Update USER Available : "+user.available);
		$scope.coderQa = {
				id : user.id,
		 available : user.available
		}
		
		if($scope.coderQa != null) {
			$http.post("web/supervisor/updateUserAvailability", $scope.coderQa)
			.success ( function(response){
				
				if ( response.satus == "OK" ) {
					alert("Coder sucessfully updated.")
				}
				
				$('#content').waitMe('hide');
				
				$scope.disabledEditor(user);
				
			}).error( function(response){
				$('#content').waitMe('hide');
				$scope.disabledEditor(user);
				alert("Error while updating coder.")
			});
		}
	}
		
    $scope.initializeUserDetails = function () {
    	if($rootScope.user) {
    		
    		$scope.loadMonitorUsersDetails();
    		
		} else {
			
	    	$http.get('web/user/userdetails').success(function(response) {
	    		if (response.status == "OK") {
	    			$rootScope.user = response.payLoad;
	    			if($rootScope.user) {
	    				$scope.loadMonitorUsersDetails();
	    			}
	    		} else {
	    			$location.path("/login");
	    		}
			}).error(function(response) {
				console.log("Error Occurred");
	        });
		}
    };
    	
    $scope.loadAllUsersName = function() {
		$http.get('web/user/detail/allUsersName').success(function(data) {
			$scope.usersName = data.payLoad;
		});
	};
	
	
	$scope.loadAllUsersRole = function() {
		$http.get('web/user/detail/allUsersRole').success(function(data) {
			$scope.userRoles = data.payLoad;
		});
	};
	
	$scope.loadAllLocations = function() {
		$http.get('web/user/detail/allLocations').success(function(data) {
			$scope.userLocations = data.payLoad;
		});
	};
	
    
	$scope.init = function () {
		$scope.initializeUserDetails();
		//$scope.loadCoders();
    };
    
	//Initialization of the controller by calling the init method.
    $scope.init();
	
};


/**
 * MonitorPerUserController
 * 
 * @param $scope
 * @param $rootScope
 * @param $http
 * @param $filter
 * @param $location
 * @param $routeParams
 * @param Restangular
 * @param ngTableParams
 * @param SearchService
 * @param SortService
 */
function MonitorPerUserController($scope, $rootScope, $http, $filter, $location, $routeParams, Restangular, ngTableParams, 
		SearchService, SortService) {
	$scope.$location = $location;
	$scope.filteredItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.searchItems = [];
	$scope.sortingOrder  = sortingOrder;
    $scope.reverse       = false;
    $scope.fromDate = "";
	
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
	
	$scope.loadProgressBar = function(){
	 	$('#content').waitMe({
	 		effect : 'roundBounce',
	 		text : 'Please wait...',
	 		bg: 'rgba(255,255,255,0.7)',
	 		color : '#000',
	 		sizeW : '',
	 		sizeH : ''
	 	});
	}
	
	//Gets the date value from date fields and assign to variable.
    $scope.fetchUserDetailsByDate  = function() {
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
        		
        		$scope.loadUserDetails();
        	}
    		$scope.validateDate = true;
    	}
    };
    
	$scope.loadUserDetails = function() {
		if ($routeParams != undefined && $routeParams.userId != undefined) {
			$scope.loadProgressBar();
       		$http.get("web/supervisor/coderqa?coderOrQaId="+$routeParams.userId+"&fromDate="+$scope.fromDate+"&toDate="+$scope.toDate).success(function(obj) {
    			$scope.coderOrQa  = obj.originalElement;
    			var data = obj.payLoad;
	    		if(data != null){
	    			$scope.userid = data.userId;
	    		    $scope.username = data.userName;
	                $scope.role = data.role;
	                $scope.primaryClient = data.primaryClient;
	                $scope.primaryChart = data.primaryChart;
	                $scope.experience = data.experience;
	                $scope.userItems =  data.chartDetails;
	    		}
				
    			// functions have been describe process the data for display
    			$scope.search();
    			
    			//Initialized the filters data
				$scope.loadAllChartStatus();
				$scope.loadAllClients();
				
				$('#content').waitMe('hide');
    			
        	}, function(e){
        		//showAlert("error", "Error retrieving Coder or QA details " + e);
        		$('#content').waitMe('hide');
        	}).error(function(data, status, headers, config){
        		$('#content').waitMe('hide');
        		//show error message
        	});
    	}
	};
		
	$scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
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
		
		$scope.searchItems = SearchService.search($scope.userItems,  $scope.query);
		
		//Filter data by client
		if($scope.queryClient && $scope.queryClient.length > 0) {
			console.log("Search By queryClient: "+$scope.queryClient)
			$scope.searchItems = SearchService.searchByClient($scope.searchItems, $scope.queryClient);
		}
		//Filter data by status
		if($scope.queryStatus && $scope.queryStatus.length > 0) {
			console.log("Search By queryStatus: "+$scope.queryStatus)
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
		}
	};
	
	//Load All clients details to use in filters
    $scope.loadAllClients = function() {
		$http.get('web/client/detail/all').success(function(data) {
			$scope.clients = data.payLoad;
		});
	};
	
	$scope.loadAllChartStatus = function() {
		/* Loads the clientconfig.properties files and assign the data to field */
		$http.get('app/clientconfig.properties').then(function (response) {
			console.log("response.data : " +response.data)
			if(response.data)
			{
				$scope.chartsStatus = response.data.monitorPerUserChartStatus;
			} 
			
		});
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
		$scope.fetchUserDetailsByDate();
	};
	
	//Initialization of the controller by calling the init method.
    $scope.init();
};
   
