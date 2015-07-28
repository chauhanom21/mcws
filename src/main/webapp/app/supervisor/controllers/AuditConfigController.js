'use strict';

/**
 * 
 */
function AuditConfigController($scope, $rootScope, $http, $filter, $routeParams, $location, SearchService) {
    $scope.$location = $location;
    $scope.fromDate;
    $scope.toDate;
    $scope.title;
    $scope.alerts = [];
    
    $scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
	
	var dt = new Date();
	dt.setDate(dt.getDate() - 30);
	$(function () {
		$('#fromdatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date()
		});
		
		$('#todatetime').datetimepicker({
			  pickTime: false,
			  maxDate: new Date(),
			  defaultDate: new Date()
		});
	});
	
	//Gets the date value from date fields and assign to variable.
    $scope.initializeDateValues  = function() {
    	var fromDateObj = $('#fromdatetime').data("DateTimePicker").getDate();
    	var toDateObj = $('#todatetime').data("DateTimePicker").getDate();
    	$scope.startDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
		$scope.endDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    	if(fromDateObj && toDateObj)
    	{  		
    		$(".ui-error-message").hide();
    		$scope.fromDate = $filter('date')(new Date(fromDateObj), 'MM/dd/yyyy');
    		$scope.toDate = $filter('date')(new Date(toDateObj), 'MM/dd/yyyy');
    		
    		return true;
    	}
    };
	
    $scope.fetchQueryValue =  function ( name ) {
		  $scope.showAlert = false;
		  $scope.alerts = [];
		  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
		  var regexS = "[\\?&]"+name+"=([^&#]*)";
		  var regex = new RegExp( regexS );
		  var results = regex.exec( window.location.href );
		  if( results == null )
			return "";
		  else
			return results[1];
    };
    

	//  createAudit function create the new audit configuration
	$scope.createAudit = function () {
		 if($scope.alerts && $scope.alerts.length > 0) {
			 for(var i=0; i < $scope.alerts.length; i++) {
				 $scope.closeAlert(i);
			 }
		 }
		 
		var validDate = $scope.initializeDateValues();
		if(validDate) {
			$scope.auditParameters = {};
			$scope.auditParameters.fromDate = $scope.fromDate;
			$scope.auditParameters.toDate = $scope.toDate;
			$scope.auditParameters.title = $scope.title;
			
			$scope.userId = $rootScope.user.userId;
			if ($scope.userId) {
				$scope.auditParameters.userId = $scope.userId;
	
				$http.post("web/supervisor/createAudit", $scope.auditParameters).success(function(response) {
					if (response.status == 'OK') {
						console.log("Sucessfully created  the auditing data !!!!")
						$scope.showAlert = true;
						$scope.alerts.push({type : 'success', message: response.message});
					} else if(response.status == 'VALIDATION_ERROR' && response.statusCode == 412){
						$scope.alerts.push({type : 'danger', message: "From date should be less than To date."});
						return false;
		          	} else {
						$scope.showAlert = true;
						$scope.alerts.push({type : 'danger', message: response.message});
					}
				}, function(e) {
					 $scope.showAlert = true;
					 $scope.alerts.push({type : 'danger', message: e});
				});
			} else {
				 $scope.showAlert = true;
				 $scope.alerts.push({type : 'danger', message: "Got Error while submiting the form,  Unable to load Owner User Id !!!!"});
			}
		}
		
    	return;
	};
		
}; // AuditConfigController
