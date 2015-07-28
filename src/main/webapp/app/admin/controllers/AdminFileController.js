'use strict';

/**
 * EmployeeController
 * @constructor
 */
var AdminFileController = function($scope, $http, $location,$routeParams) {

    $scope.document = {};
    $scope.alerts = [];
    $scope.myClient ;
    $scope.isIncompleteList;
    
    $scope.loadProgressBar = function(){
	 	$('#content').waitMe({
	 		effect : 'win8',
	 		text : 'Please wait. Your request in progress',
	 		bg: 'rgba(255,255,255,0.7)',
	 		color : '#000',
	 		sizeW : '',
	 		sizeH : ''
	 	});
	}
    
    $scope.init = function ($location,$routeParams) {
    	var clientsList = [];
	    	$http.get('web/client/detail/all').success(function (data) {
	    		clientsList = data.payLoad;
	            console.log("clientsList :" + clientsList);
	            $scope.clientsList = clientsList;
	            });	    
    };
    
    //Initialization of the controller by calling the init method.
    $scope.init($location,$routeParams);
       
	$scope.uploadFile = function() {
		
		$scope.showAlert = false;
		$scope.alerts = [];
		$scope.canSaveGrants = true;
		// remove success messages
		$('#messages').removeClass('alert alert-success').empty();

		
		if (file.files == null || file.files.length == 0) {
		    $scope.alerts.push({type: 'danger', msg: "NO Excel file selected. Please Browse a valid .xls(97-2003 Worksheet) file and import it."});
			$scope.showAlert = true;
			return;
		}
		
		var formData = new FormData();
		formData.append("file", file.files[0]);
		formData.append("myClient", $scope.myClient);
		formData.append("isIncompleteList",$scope.isIncompleteList);
		$http({
			method : 'POST', url : 'web/worklist/upload',
			headers : {
				'Content-Type' : undefined
			},
			data : formData, 
			transformRequest: angular.identity
		}).success(function(response, status) {
			//$scope.employeeRecordsRef = response.payLoad;
			//console.log($scope.employeeRecordsRef.employeeRecords.length);
			$scope.message = response.message;
			if (response.status == 'ERROR') {
				$scope.alerts.push({type: 'danger', msg: response.message});
				console.log('Uploading failed');
				$scope.showAlert = true;
				$scope.canSaveGrants = false;
			} else {
				$scope.alerts.push({msg: response.message});
				console.log('updating the message');
				$scope.showAlert = true;
				//$('#messages').addClass('alert alert-success').append('<p> Submitted Successfully </p>');
				$scope.myClient = "";
				$scope.document.fileInput = [];				
			}
		}).error(function(data, status) {
		    $scope.alerts.push({type: 'danger', msg: "Error during Import: " + status});
			$scope.showAlert = true;			
			return;
		});
	};

	
	
	$scope.setupCodersDailyWorkload = function(){
		
		$scope.loadProgressBar();
		
		$http.get('web/supervisor/setupDailyCodersWorkload').success(function (data) {
				if(data.status == "OK") {
					alert(" Coders daily workload assignment completed successfully !");
				}  else {
					alert (" Error on setup coders daily workload !")
				}
				$('#content').waitMe('hide');
        }).error(function(data, status) {
        	$('#content').waitMe('hide');
        	alert (" Error on setup coders daily workload")       	
			return;
		});	    
	}
	
	$scope.resetDailyWorkload = function() {
		
		$scope.loadProgressBar();
		
		$http.get('web/supervisor/resetDailyCodersWorkload').success(function (data) {
			if(data.status == "OK") {
				alert(" Successfully reset the Daily workload")
			}  else {
				alert (" Error : Reseting Daily Workload")
			}
			$('#content').waitMe('hide');
		});	    
	}
	
	$scope.saveGrants = function(employeeRecordsRef) {};

	$scope.saveSchemeDetails = function(scheme) {};
		
	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};	
   
};