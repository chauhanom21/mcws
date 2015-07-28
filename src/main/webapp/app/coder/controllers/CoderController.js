'use strict';

/**
 * CoderController
 * @constructor
 */
var CoderController = function($scope, $http, $location, $routeParams, $filter, $modal, $rootScope, ngDialog, 
		LinkHandlerService, SortService) {
	
	$scope.userId = null;
	$scope.itemsPerPage = 5;
	$scope.pagedItems = [];
	$scope.currentPage = 0;
	$scope.alerts = [];
	$scope.worklists = [];
	$scope.showAlert = null;
	$scope.messageAlert = [];
	$scope.enableFetch = false;
	$scope.enableCoderFetch = false;
	
    
	$scope.fetchAllTasks = function() {
		$scope.showAlert = false;
		$scope.alerts = [];
        $http.get('coders/fetch').success(function(carList){
            $scope.cars = carList;
        });
    };
    
	
    $scope.fetchActiveOrAssignedTasks = function() {
		$scope.showAlert = false;
		$scope.alerts = [];
//		console.log("==> Username :: "+ $rootScope.user.name);
//		console.log("==> UserId :: "+ $rootScope.user.userId);
//		console.log("==> UserRole :: "+ $rootScope.user.userRole);
		
    	$http.post('web/coder/fetchActiveCharts', $rootScope.user.userId).success(function(response, status) {
			if (response.status == 'ERROR') {
				$scope.messageAlert = "Unable to load task detail, Got error while loading !";
				$scope.showAlert = true;	
			} else {
	    		$scope.enableFetch = response.enableTaskOption;
    			$scope.enableCoderFetch = response.enableCoderTaskOption;
				$scope.message = response.message;
				$scope.worklists = response.payLoad;
				if(response.payLoad == null){
					$scope.messageAlert =response.message;// "No work items found for You! Please contact Supervisor/Admin immediately";
					$scope.showAlert = true;
				}
			}
			if($scope.showAlert){
				$scope.openPopup($scope.alerts);
        	} else {
        		if($scope.currentPage > 0){
        			$scope.currentPage = $scope.currentPage;
        		} else {
        			$scope.currentPage = 0;
        		}
    			// now group by pages
    			$scope.groupToPages();
    			//For pagination
    			if($scope.worklists && $scope.worklists.length > 0){
    				$scope.maxSize = 3;
    				$scope.bigTotalItems = $scope.worklists.length;
    				$scope.bigCurrentPage = 0;
    				$scope.currentPage = 0;
    			} else {
    				$scope.maxSize = 0;
    				$scope.currentPage = 0;
    			}
    			/*pagination code done */
        	}		
		}).error(function(response) {
           console.log('Could not Fetch Tasks'+ response.message);
        }); 
    };
	    
    $scope.fetchMyTask = function(isCoderTask) { 
		$scope.showAlert = false;
		$scope.alerts = [];
		$scope.productData = {};
		$scope.productData.userId = $rootScope.user.userId;
		$scope.productData.coderTask = isCoderTask;

		$scope.enableFetch = false;
        $http.post('web/coder/fetchchart', $scope.productData).success(function(response, status) {
        	$scope.enableFetch = response.enableTaskOption;
    		
        	if(response.status == 'OK') {        		
        		if( response.payLoad == null) {
    				$scope.messageAlert = response.message;;					
    				$scope.showAlert = true;
    				$scope.worklists = [];
    			} else {
    				$scope.message = response.message;
        			$scope.worklists.push(response.payLoad); 
        			
        			
        			$scope.sortedWorklist =  SortService.sortWorkItemByUpdateDateDESC($scope.worklists, 'updateDateMiliis');
        			
        			if($scope.sortedWorklist && $scope.sortedWorklist.length > 0) {
        				for (var i = 0; i < $scope.sortedWorklist.length; i++) {
        					console.log( "ID : "+$scope.sortedWorklist[i].id)
        				}
        			}
        			if($scope.currentPage > 0){
            			$scope.currentPage = $scope.currentPage;
            		} else {
            			$scope.currentPage = 0;
            		}
        			// now group by pages
        			$scope.groupToPagesSortedItems();
        			//For pagination
        			if($scope.sortedWorklist && $scope.sortedWorklist.length > 0){
        				$scope.maxSize = 3;
        				$scope.bigTotalItems = $scope.sortedWorklist.length;
        				$scope.bigCurrentPage = 0;
        				$scope.currentPage = 0;
        			} else {
        				$scope.maxSize = 0;
        				$scope.currentPage = 0;
        			}
        			/*pagination code done */
        		}
        		$scope.enableFetch = response.enableTaskOption;
        		$scope.enableCoderFetch = response.enableCoderTaskOption;
        	} else {
        		$scope.messageAlert = "Unable to load task detail, Got error while loading !";
				$scope.showAlert = true;
				
				$scope.enableFetch = response.enableTaskOption;
        		$scope.enableCoderFetch = response.enableCoderTaskOption;
			} 
        	if($scope.showAlert){
        		$scope.openPopup($scope.messageAlert);
        	} else {
        		// For paging
    			$scope.currentPage = 0;
    			// now group by pages
    			$scope.groupToPages();
        	}
        	console.log("Enable or Disable========>>>"+$scope.enableFetch);
		}).error(function(response) {
			console.log("Error Occurred");
            //$scope.setError('Could not Fetch Task'+response.getMessage());
        });
    };
	
	$scope.fetchUserRole =  function () {
		  if($rootScope.user) {
			  if( ($rootScope.user.userRole).indexOf("QA") > 0){
				  return "qa";
			  }
		  } else {
			  return "";
		  }
    };


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
    
     /**
      * Open the coding form where coder/qa can do the coding of the selected chart
      */ 
	 $scope.getTaskDetails = function (taskId, chartType) {
		$scope.$parent.selectedTaskID = taskId;
		$scope.$parent.selectedTaskChartType = chartType;
		$scope.$parent.audit = false;
		$("#task-edit-row-button").attr('disabled','disabled');
		$http.post('web/coder/updateStatusToInProgress/'+taskId).success(function(response){
        	if (response.status == "OK") {
        		$("#task-edit-row-button").removeAttr('disabled');
        		$scope.notes = response.payLoad;
        		//disable all coder/qa menubar links.
        	 	LinkHandlerService.disableCoderQAMenubarLinks();
        	 	$scope.loadProgressBar();
        		$location.path('/coding_chart/'+chartType.toLowerCase()+'/'+taskId+'/'+false);
        		
        	} else {
        		$("#task-edit-row-button").removeAttr('disabled');
        		console.log("Got error while updating the status !");
        	}
        });
    };
    
	$scope.openWarningPopup = function () {
		$scope.value = true;
		ngDialog.open({
			template: 'app/coder/views/code_form_refresh_warn.html',
			className: 'ngdialog-warning-theme-plain',
			scope: $scope
		});
	};
	
    // calculate page in place
	$scope.groupToPages = function () {
		$scope.pagedItems = [];
		if($scope.worklists && $scope.worklists.length > 0) {
			for (var i = 0; i < $scope.worklists.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.worklists[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.worklists[i]);
				}
			}
		}
	};
	
	// calculate page in place
	$scope.groupToPagesSortedItems = function () {
		$scope.pagedItems = [];
		if($scope.sortedWorklist && $scope.sortedWorklist.length > 0) {
			for (var i = 0; i < $scope.sortedWorklist.length; i++) {
				if (i % $scope.itemsPerPage === 0) {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.sortedWorklist[i] ];
				} else {
					$scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.sortedWorklist[i]);
				}
			}
		}
	};
	
	$scope.setPage = function () {
		$scope.currentPage = $scope.bigCurrentPage - 1;
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
    
    $scope.loadUserNotesValue = function() {
    	if($rootScope.user){
    		$http.get('web/coder/loadUserNotesValue/'+$rootScope.user.userId).success(function(response){
        		if (response.status == "OK") {
        			$scope.notes = response.payLoad;
        		} else {
        			console("Got error while loading user's notes!");
        		}
        	});
    	} else {
			$location.path("/mytask");
		}
    	
    }
    
    $scope.updateUserNotes = function(){
    	
    	$http.post('web/coder/saveOrUpdateNotes/'+$rootScope.user.userId, $scope.notes).success(function(response) {
        	if (response.status == "OK") {
    			alert("Successfully updated!");
    		} else {
    			alert("Update Failed!");
    		}
		}).error(function(response) {
			alert("Error Occurred");
        });
    }
    
    
    $scope.initializeUserDetails = function () {
    	$http.get('web/user/userdetails/').success(function(response) {
    		if (response.status == "OK") {
    			$rootScope.user = response.payLoad;
    			if($rootScope.user) {
    				$scope.fetchActiveOrAssignedTasks();
    			}
    		} else {
    			$location.path("/login");
    		}
		}).error(function(response) {
			console.log("Error Occurred");
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
