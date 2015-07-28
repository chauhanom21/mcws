'use strict';

pointyApp.controller('UserController',
    function UserController($scope, PatientService, $routeParams) {
	    $scope.showFeedback = false;
	    	    
	    /**
	     * Save a patient
	     */
	    $scope.savePatient = function (patient, form) {
        	console.log(patient);
        	
            if(form.$valid) {
            	var aPromise = PatientService.save(patient);
            	aPromise.then(function(object){
            		patient.id = object.id;
            		showAlert("success", "Patient registered successfully! Just went over to pointy-api webapp using Restangular and saved this patient!");
            	}, function errorCallback(error) {
            		showAlert("error", error);
            	});
            } else {
            	showAlert("error", "Invalid form: " + form);
            }
        };

    }
);