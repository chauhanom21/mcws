'use strict';

/**
 * TaskDetailsController
 * @constructor
 */
var TaskDetailsController = function($scope, $http, $location, $rootScope, $routeParams, 
		$filter, $modal, $log, ngDialog, CoderFormService, LinkHandlerService) {
	
	$scope.$location = $location;
	$scope.audit = false;
	$scope.userId = null;
	$scope.userRole = null;
	$scope.groupedItems = [];
	$scope.filteredItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.alerts = [];
	$scope.chartDetail = {};
	$scope.showAlert = null;	
	$scope.codingChart = {};
	$scope.ipChart = {};
	$scope.columnLength = {};
	$scope.ipChart.account = [];
	$scope.ipChart.sdx = [];
	$scope.ipChart.sdxpoa = [];	
	$scope.ipChart.dos = [];
	$scope.ipChart.icdGroup = [];
	$scope.ipChart.rcGroup = [];
	$scope.ipChart.ipIcdGroup = [];
	$scope.ipChart.geCptGroup = [];
	$scope.ipChart.sdxpoaGroup = [];
	$scope.ipChart.mod = [];
	$scope.ipChart.serviceType = [];
	$scope.ipChart.dx = [];
	$scope.insuranceOtherReq = false; 
	$scope.insuranceOther = false;
	$scope.options = [];
	$scope.comments = [];
	$scope.taskDetailsHistory = [];
	$scope.colValCount = [];
	$scope.startTime = 0;
	$scope.showTimer = false;	
	$scope.disCoderLink = true;
	$scope.dispalyTaskDetails = false;
	
	$scope.showTaskDetails = function(){
		$scope.dispalyTaskDetails = true;
	}
	$scope.hideTaskDetails = function(){
		$scope.dispalyTaskDetails = false;
	}
	
	
	$scope.loadSubmitProgressBar = function(){
	 	$('#content').waitMe({
	 		effect : 'win8',
	 		text : 'Please wait, submission in-progress.',
	 		bg: 'rgba(255,255,255,0.7)',
	 		color : '#000',
	 		sizeW : '',
	 		sizeH : ''
	 	});
	}
	
	/**
	 * It calls when Controller loads
	 */
	$scope.editTask = function () {	
		
		console.log(" Calling Edit task function to load chart details.. : ")
		console.log(" routeParams.audit : "+$routeParams.audit)
		if ($routeParams != undefined && $routeParams.taskId != undefined) {
			 $scope.$parent.selectedTaskID = Number($routeParams.taskId);
		}
		if ($routeParams != undefined && $routeParams.audit != undefined) {
			 $scope.$parent.audit = $routeParams.audit;
		}
		if ($routeParams != undefined && $routeParams.type != undefined) {
			 $scope.chartType = ($routeParams.type).toUpperCase();
		} else {
			$scope.chartType = $scope.$parent.selectedTaskChartType;
		}
		
		
		$scope.queryData = {};
		$scope.queryData.taskId = $scope.$parent.selectedTaskID;
		$scope.queryData.userId = $rootScope.user.userId;
		 
		$http.post('web/coder/fetchchartdetails', $scope.queryData).success(function(response, status) {
			$scope.message = response.message;
		
			if (response.status == 'OK'){
				$scope.chartDetail = response.payLoad;
				$scope.ipChart.accountNumber = $scope.chartDetail.accountNumber;
				$scope.ipChart.mrNumber = $scope.chartDetail.mrNumber;
				$scope.ipChart.chartType = $scope.chartDetail.chartType;
				$scope.ipChart.client = $scope.ipChart.client;
				$scope.ipChart.id = $scope.chartDetail.id;
				$scope.ipChart.coderId = $scope.chartDetail.coderId;
				$scope.ipChart.client = $scope.chartDetail.client;
				$scope.ipChart.em = $scope.chartDetail.em;
				$scope.ipChart.taskAssignedTime = $scope.chartDetail.taskAssignedTime;
				
				console.log("$scope.chartDetail.totalWorkTime : "+$scope.chartDetail.totalWorkTime)
				//Added Timer
		        $scope.startTime = (new Date().getTime()) - ($scope.chartDetail.totalWorkTime);
				$scope.showTimer = true;
				if($scope.chartDetail) {
					if($scope.chartDetail.columnValCount && $scope.chartDetail.columnValCount != null) {
						$scope.colValCount = $scope.chartDetail.columnValCount;
						$scope.colValCount.poa = $scope.colValCount.pdxpoa + $scope.colValCount.sdx_poa;
					}
					var codeData = JSON.parse($scope.chartDetail.codingData);
					if(codeData && codeData.insurance && codeData.insurance == 'other') {
						$scope.insuranceOtherReq = true; 
					} else {
						$scope.insuranceOtherReq = false; 
					}
					$scope.taskDetailsHistory = $scope.chartDetail.workitemHistory;
				}
				
				console.log("---> IS Audit: "+$scope.$parent.audit)
				// This code load the audit chart Weightage.
				if($scope.$parent.audit && $scope.$parent.audit == 'true') {
					console.log("---> IS Audit TRUE: Loading Audit Data...")
					$scope.populateGradingSheet();
				}

				$scope.role = $scope.chartDetail.userRole;
				
				/* Loads the clientconfig.properties files and assign the data to field */
				$http.get('app/clientconfig.properties').then(function (response) {
					if($scope.role == response.data.usersRole.Coder)
					{
						$scope.options = response.data.coderOptions;
					} 
					else if($scope.role == response.data.usersRole.LocalQA)
					{
						$scope.options = response.data.localQAOptions;
					}
					else if($scope.role == response.data.usersRole.RemoteQA)
					{
						$scope.options = response.data.remoteQAOptions;
					}
				});
				
				$scope.constructCodingFormDetails();
			} else {
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;	
			} 
		}).error(function(response) {
			console.log("Error Occurred");
        });
    };
    
    /**
     * Construct the form element.
     */
    $scope.constructCodingFormDetails = function() {
    	if($scope.chartDetail.codingData && $scope.chartDetail.codingData.length > 0) {
			var obj = JSON.parse($scope.chartDetail.codingData);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.account, obj.account);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.sdx, obj.sdx);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.sdxpoa, obj.sdxpoa);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.dos, obj.dos);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.mod, obj.mod);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.serviceType, obj.serviceType);
			CoderFormService.populateMutliValueFormFields($scope.ipChart.dx, obj.dx);
			CoderFormService.populateGroupFormFields($scope.ipChart.icdGroup, obj.icdGroup);
			CoderFormService.populateIpIcdGroupFormFields($scope.ipChart.ipIcdGroup, obj.ipIcdGroup);
			CoderFormService.populateGECptGroupFormFields($scope.ipChart.geCptGroup, obj.geCptGroup);
			CoderFormService.populateSdxpoaGroupFormFields($scope.ipChart.sdxpoaGroup, obj.sdxpoaGroup);
			CoderFormService.populateRCGroupFormFields($scope.ipChart.rcGroup, obj.rcGroup);
			
			// Populates Single value fileds of Coding Charts Form
			CoderFormService.populateSingleValueFormFields(obj, $scope.ipChart);
		} else {
			$scope.ipChart.account = [{'name':'account'}];
			$scope.ipChart.sdx = [{'name':'sdx'}];
			$scope.ipChart.sdxpoa = [{'name':'sdx_poa'}];	
			$scope.ipChart.dos = [{'name':'dos'}];
			$scope.ipChart.mod = [{'name':'mod'}];
			$scope.ipChart.serviceType = [{'name':'serviceType'}];
			$scope.ipChart.dx = [{'name':'dx'}];
			$scope.ipChart.icdGroup = [{ "icd":[{'name':'icd'}], 
										 "cpt":[{'name':'cpt'}], 
										 "mod":[{'name':'mod'}]
									  }];
			$scope.ipChart.rcGroup = [{ "revenueCode":[{'name':'icd'}], 
				 						"dos":[{'name':'cpt'}], 
				 						"phy":[{'name':'mod'}]
			  						  }];
			$scope.ipChart.ipIcdGroup = [{ "icd":[{'name':'icd'}], 
											"dos":[{'name':'dos'}],
											"phy":[{'name':'phy'}]
										  }];
			$scope.ipChart.geCptGroup = [{ "cpt":[{'name':'cpt'}], 
											"mod":[{'name':'mod'}],
											"dx":[{'name':'dx'}]
										  }];
			$scope.ipChart.sdxpoaGroup = [{ "sdx":[{'name':'sdx'}], 
											"sdx_poa":[{'name':'sdx_poa'}]
										  }];
		}
    };
    
    /**
     * Submit Coding Form
     */
    $scope.submit = function () {	
    	console.log("<=======submit the FORM request====>");
    	
    	$scope.loadSubmitProgressBar();
    	
    	$("#submit_button").attr('disabled','disabled');
    	$("#save_button").attr('disabled','disabled');
    	$("#cancel_button").attr('disabled','disabled');
    	$scope.ipChart.coderName = $rootScope.user.name;
    	$scope.ipChart.userRole = $rootScope.user.userRole;
    	$scope.codingChart.codingData = $scope.ipChart;
    	$scope.codingChart.tempData = false;
    	if($scope.ipChart.insurance){
    		$scope.codingChart.insurance = $scope.ipChart.insurance;
    	} else {
    		$scope.codingChart.insurance = $scope.ipChart.insuranceOther;
    	}
    	
		$http.post('web/coder/saveCodingChartData', $scope.codingChart).success(function(response, status) {
			$scope.message = response.message;
			console.log("IP Chart=>"+$scope.ipChart);
			if (response.status == 'OK') {
				//Enables user's menu links
				LinkHandlerService.enableCoderQAMenubarLinks();
				$("#submit_button").removeAttr('disabled');
				$("#save_button").removeAttr('disabled');
				$("#cancel_button").removeAttr('disabled');
				$location.path('/mytasks');
			} else {				
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;
				$("#submit_button").removeAttr('disabled');
				$("#save_button").removeAttr('disabled');
				$("#cancel_button").removeAttr('disabled');
			}			
			
			$('#content').waitMe('hide');
		}).error(function(response) {
			$('#content').waitMe('hide');
			console.log("Error Occurred");
            //$scope.setError('Could not Fetch Task'+response.getMessage());
        });
    };

    /**
     * Save Coding Form
     */
    $scope.saveCodingForm = function () {	
    	
    	$scope.loadSubmitProgressBar();
    	
    	console.log("<=======SAVING Coding Form Data====>");
    	
		$("#submit_button").attr('disabled','disabled');
    	$("#save_button").attr('disabled','disabled');
    	$("#cancel_button").attr('disabled','disabled');
    	
    	$scope.ipChart.status = $scope.chartDetail.status;
    	$scope.ipChart.coderName = $rootScope.user.name;
    	$scope.ipChart.userRole = $rootScope.user.userRole;
    	$scope.codingChart.codingData = $scope.ipChart;
    	if($scope.ipChart.insurance){
    		$scope.codingChart.insurance = $scope.ipChart.insurance;
    	} else {
    		$scope.codingChart.insurance = $scope.ipChart.insuranceOther;
    	}
    	$scope.codingChart.tempData = true;
    	
		$http.post('web/coder/saveCodingChartData', $scope.codingChart).success(function(response, status) {
			$scope.message = response.message;
			console.log("IP Chart=>"+$scope.ipChart);
			if (response.status == 'OK') {
				$scope.alerts.push({type: 'success', msg: 'Form data has been saved successfully'});
				$scope.showAlert = true;
				$("#submit_button").removeAttr('disabled');
				$("#save_button").removeAttr('disabled');
				$("#cancel_button").removeAttr('disabled');
			} else {				
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;
				$("#submit_button").removeAttr('disabled');
				$("#save_button").removeAttr('disabled');
				$("#cancel_button").removeAttr('disabled');
				//Enables user's menu links
				LinkHandlerService.enableCoderQAMenubarLinks();
				$location.path('/mytasks');
			}	
			
			$('#content').waitMe('hide');
			
		}).error(function(response) {
			
			$('#content').waitMe('hide');
			
			console.log("Error Occurred");
            //$scope.setError('Could not Fetch Task'+response.getMessage());
        });
    };
	
	$scope.addNewRow = function(fieldName, index) {
		if(fieldName && fieldName.length > 0){
			if(fieldName=="sno") {
				CoderFormService.addNewRow("sno", $scope.ipChart.sno, index);
			}
			if(fieldName=="adx") {
				CoderFormService.addNewRow("adx", $scope.ipChart.adx, index);
			} 
			else if(fieldName=="account")
			{
				CoderFormService.addNewRow("account", $scope.ipChart.account, index);
			} 
			else if(fieldName=="pdx")
			{
				CoderFormService.addNewRow("pdx", $scope.ipChart.pdx, index);
			} 
			else if(fieldName=="pdxpoa")
			{
				CoderFormService.addNewRow("pdxpoa", $scope.ipChart.pdxpoa, index);
			}
			else if(fieldName=="sdx")
			{
				CoderFormService.addNewRow("sdx", $scope.ipChart.sdx, index);
			} 
			else if(fieldName=="sdxpoa")
			{
				CoderFormService.addNewRow("sdxpoa", $scope.ipChart.sdxpoa, index);
			}
			else if(fieldName=="mod")
			{
				CoderFormService.addNewRow("mod", $scope.ipChart.mod, index);
			} 
			else if(fieldName=="rev")
			{
				CoderFormService.addNewRow("rev", $scope.ipChart.rev, index);
			} 
			else if(fieldName=="dos")
			{
				CoderFormService.addNewRow("dos", $scope.ipChart.dos, index);
			} 
			else if(fieldName=="drg")
			{
				CoderFormService.addNewRow("drg", $scope.ipChart.drg, index);
			} 
			else if(fieldName=="disp")
			{
				CoderFormService.addNewRow("disp", $scope.ipChart.disp, index);
			} 
			else if(fieldName=="serviceType")
			{
				CoderFormService.addNewRow("serviceType", $scope.ipChart.serviceType, index);
			} 
			else if(fieldName=="dx")
			{
				CoderFormService.addNewRow("dx", $scope.ipChart.dx, index);
			}
		}
		 else {
			return false;
		}
	}
	
	$scope.removeRow = function(fieldName, index) {
		if(fieldName && fieldName.length > 0){
			if(fieldName=="sno") {
				CoderFormService.removeRow($scope.ipChart.sno, index);
			}
			if(fieldName=="adx") {
				CoderFormService.removeRow($scope.ipChart.adx, index);
			} 
			else if(fieldName=="account")
			{
				CoderFormService.removeRow($scope.ipChart.account, index);
			} 
			else if(fieldName=="pdx")
			{
				CoderFormService.removeRow($scope.ipChart.pdx, index);
			} 
			else if(fieldName=="pdxpoa")
			{
				CoderFormService.removeRow($scope.ipChart.pdxpoa, index);
			}
			else if(fieldName=="sdx")
			{
				CoderFormService.removeRow($scope.ipChart.sdx, index);
			} 
			else if(fieldName=="sdxpoa")
			{
				CoderFormService.removeRow($scope.ipChart.sdxpoa, index);
			}
			else if(fieldName=="mod")
			{
				CoderFormService.removeRow($scope.ipChart.mod, index);
			} 
			else if(fieldName=="rev")
			{
				CoderFormService.removeRow($scope.ipChart.rev, index);
			} 
			else if(fieldName=="dos")
			{
				CoderFormService.removeRow($scope.ipChart.dos, index);
			} 
			else if(fieldName=="drg")
			{
				CoderFormService.removeRow($scope.ipChart.drg, index);
			} 
			else if(fieldName=="disp")
			{
				CoderFormService.removeRow($scope.ipChart.disp, index);
			} 
			else if(fieldName=="serviceType")
			{
				CoderFormService.removeRow($scope.ipChart.serviceType, index);
			} 
			else if(fieldName=="dx")
			{
				CoderFormService.removeRow($scope.ipChart.dx, index);
			} 
		}
		 else {
			return false;
		}
	}

	//For sdx and poa combination have separate function to Add/Remove new textbox field.
	$scope.addSdxNewRow = function (index) {
		$scope.ipChart.sdxpoaGroup.splice(index+1, 0,
				{"sdx":[{'name':'sdx'}], 
				"sdx_poa":[{'name':'sdx_poa'}]
			    }) ;
    };
	
	$scope.removeSdxRow = function (index) {
		if($scope.ipChart.sdxpoaGroup.length == 1) {
			 alert("No more textbox to remove.");
           return false;
		}
		$scope.ipChart.sdxpoaGroup.splice(index, 1);
	};
	 
	$scope.addICDGroupRow = function(index) {
		$scope.ipChart.icdGroup.splice(index+1, 0,
				{"icd":[{'name':  "icd"+ $scope.ipChart.icdGroup.length}], 
				 "cpt":[{'name':  "cpt"+ $scope.ipChart.icdGroup.length}], 
				 "mod":[{'name':  "mod"+ $scope.ipChart.icdGroup.length}]
				});
	}
	$scope.removeICDGroupRow = function(index) {
		if($scope.ipChart.icdGroup.length == 1) {
			 alert("No more textbox to remove.");
            return false;
		}
		
		$scope.ipChart.icdGroup.splice(index, 1);
	}
	
	$scope.addRCGroupRow = function(index) {
		var values = $scope.ipChart.rcGroup[index];
		var revCode, dos, phy;
		angular.forEach(values, function(value, key){
			angular.forEach(value, function(item){
				if( key == "revenueCode"){
					revCode = item.value;
				} else if( key == "dos"){
					dos = item.value;
				} else if( key == "phy"){
					phy = item.value;
				}
			});
		});
		$scope.ipChart.rcGroup.splice(index+1, 0,
				{"revenueCode":[{'name':  "revenueCode"+ $scope.ipChart.rcGroup.length, 'value':revCode}],
				 "dos":[{'name':  "dos"+ $scope.ipChart.rcGroup.length, 'value':dos}],
				 "phy":[{'name':  "phy"+ $scope.ipChart.rcGroup.length, 'value':phy}]
				});
	}
	$scope.removeRCGroupRow = function(index) {
		if($scope.ipChart.rcGroup.length == 1) {
			 alert("No more textbox to remove.");
            return false;
		}
		
		$scope.ipChart.rcGroup.splice(index, 1);
	}
	
	$scope.addGECPTGroupRow = function(index) {
		$scope.ipChart.geCptGroup.splice(index+1, 0, {
			"cpt":[{'name':  "cpt"+ $scope.ipChart.geCptGroup.length}],
			"mod":[{'name':  "mod"+ $scope.ipChart.geCptGroup.length}],
			"dx":[{'name':  "dx"+ $scope.ipChart.geCptGroup.length}],
			});
	}
	$scope.removeGECPTGroupRow = function(index) {
		if($scope.ipChart.geCptGroup.length == 1) {
			 alert("No more textbox to remove.");
            return false;
		}
		$scope.ipChart.geCptGroup.splice(index, 1);
	}
	
	$scope.addIpIcdGroupRow = function(index) {
		$scope.ipChart.ipIcdGroup.splice(index+1, 0, {
			"icd":[{'name':  "icd"+ $scope.ipChart.ipIcdGroup.length}],
			"dos":[{'name':  "dos"+ $scope.ipChart.ipIcdGroup.length}],
			"phy":[{'name':  "phy"+ $scope.ipChart.ipIcdGroup.length}],
			});
	}
	
	$scope.removeIpIcdGroupRow = function(index) {
		if($scope.ipChart.ipIcdGroup.length == 1) {
			alert("No more textbox to remove.");
            return false;
		}
		$scope.ipChart.ipIcdGroup.splice(index, 1);
	}
	
    /**
     * Code related to Audit functinality
     */
    $scope.userId = null;
	$scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.alerts = [];
	$scope.workItems = [];
	$scope.showAlert = null;
	$scope.messageAlert = [];
	$scope.disableFetch = false;
	$scope.showRequired = true;
	$scope.showAlert = null;	
	
	$scope.gradSheet = {};
	$scope.gradSheetData = [];
	$scope.weightage = {};

	$scope.populateGradingSheet = function () {	
		console.log("AUDIT SHEET DATA POPULATE ======")
		$("#accuracy-div").hide();
		if($scope.$parent.selectedTaskID) {
			$http.get('web/coder/loadAuditChartDetails?taskId='+$scope.$parent.selectedTaskID
					+'&userId='+$rootScope.user.userId).success(function(response, status) {
				if (response.status == 'ERROR') {
					$scope.alerts.push({type: 'danger', msg: response.message});
					$scope.showAlert = true;	
				} else {
					$scope.message = response.message;
					$scope.gradSheetData = response.payLoad;
					
					$http.get('web/coder/getWeightageMasterDetails?chartType='+$scope.gradSheetData.chartType)
					.success(function(response, status) {
						if (response.status == 'OK') {
							$scope.message = response.message;
							$scope.weightage = response.payLoad;
						} else {				
							$scope.alerts.push({type: 'danger', msg: response.message});
							$scope.showAlert = true;
						}			
					}).error(function(response) {
						console.log("Error Occurred");
			        });
				}
			}).error(function(response) {
				console.log("Error Occurred");
			});
		}
		
    };
    
    /**
     * This method save the auditing data..
     */
     $scope.submitGradingSheet = function () {	
    	$scope.gradSheetData.gradingsheet = $scope.gradSheet;
    	if($scope.gradSheetData.drg) {
    		$scope.gradSheetData.drg = $scope.gradSheetData.drg;
    	} else {
    		$scope.gradSheetData.drg = false;
    	}
    	$scope.gradSheetData.userAudited = $rootScope.user.userId;
		$http.post('web/coder/saveAuditChart', $scope.gradSheetData).success(function(response, status) {
			if (response.status == 'OK') {
				$scope.message = response.message;
				//Enables user's menu links
				LinkHandlerService.enableCoderQAMenubarLinks();
				
				$location.path('/audit');
			} else {				
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;
			}			
			
		}).error(function(response) {
			console.log("Error Occurred");
        });
    };
    
     $scope.grandAccuracy = 0.0;
     $scope.totalAccuracy = 100.0;
     $scope.temp = 0.0;
     $scope.showColCodesError = false;
     $scope.disableAuditForm = false;
     $scope.columnErrors = [];
      // this function used to Calculate the Accuracy.
     $scope.calColumnAccuracy = function(weightage, codes, error, colName) {
    	 if(weightage >= 0 && error && parseFloat(codes) >=0) {
    		 if(parseFloat(error) > parseFloat(codes)) {
    			 $scope.showColCodesError = true; 
    			 $scope.disableAuditForm = true;
    			 if($scope.columnErrors  && $scope.columnErrors .length > 0) {
    				 for(var i = 0; i < $scope.columnErrors.length; i++) {
    					 if($scope.columnErrors[i].name == colName){
    						 return;
    					 } 
    				 }
    			 } else {
    				 $scope.columnErrors.push({name:colName});
    				 return;
    			 }
    			 
    			 return;
    		 } else {
    			 	 var errors = $scope.validateAuditForm($scope.columnErrors, colName);
    			 	 if(errors && errors.length > 0) {
    			 		 $scope.showColCodesError = true;
            			 $scope.disableAuditForm = true;
    			 		 return;
    			 	 }
    				 $scope.showColCodesError = false;
        			 $scope.disableAuditForm = false;
        			 $("#accuracyError").hide();
        			 $scope.valueAccuracy = 0.0;
            		 
            		 CoderFormService.calculateAccuracy(weightage, error, codes, colName, $scope.gradSheet);
            		 $scope.temp = $scope.calculateTotalAccuracy($scope.gradSheet, $scope.valueAccuracy);
            		 $scope.grandAccuracy =  $scope.temp;
            		 $scope.totalAccuracy = (100 - $scope.grandAccuracy).toFixed(2);
            		 
            		 if($scope.totalAccuracy < 0.0) {
            			 $("#accuracyError").show();
            			 $scope.disableAuditForm = true;
            		 } else {
            			 $("#accuracyError").hide();
            		 }
            		 $scope.gradSheetData.totalAccuracy = $scope.totalAccuracy; 
    		 }
    	 }
     };
     
	  $scope.validateAuditForm = function(columnErrors, columnName) {
		  if(columnErrors  && columnErrors.length > 0) {
			 $scope.errorRemain = [];
			 for(var i = 0; i < $scope.columnErrors.length; i++) {
				 if(columnErrors[i].name == columnName) {
					 columnErrors.splice(columnErrors[i]);
				 }  else {
					 $scope.errorRemain.push(columnErrors[i]);
				 }
			 }
		 }
		 return $scope.errorRemain;
	  }
     
      $scope.calculateTotalAccuracy = function (gradSheetObj, totalAccuracy) 
      {
    	if(parseFloat(gradSheetObj.adxAccuracy) > 0)	{
  			 totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.adxAccuracy);
  		}
    	if (parseFloat(gradSheetObj.pdxAccuracy) > 0 ) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.pdxAccuracy);
		}  
        if (parseFloat(gradSheetObj.sdxAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.sdxAccuracy);
		} 
    	if (parseFloat(gradSheetObj.mccAccuracy) > 0) {
			 totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.mccAccuracy);
		} 
    	if(parseFloat(gradSheetObj.icdAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.icdAccuracy);
		} 
    	if(parseFloat(gradSheetObj.drgAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.drgAccuracy);
		}
    	if(parseFloat(gradSheetObj.dispAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.dispAccuracy);
		} 
    	if(parseFloat(gradSheetObj.physicianAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.physicianAccuracy);
		} 
    	if(parseFloat(gradSheetObj.dosAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.dosAccuracy);
		} 
    	if(parseFloat(gradSheetObj.revenueCodeAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.revenueCodeAccuracy);
		}
    	if(parseFloat(gradSheetObj.modAccuracy) > 0) {
    		totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.modAccuracy);
		}
    	if(parseFloat(gradSheetObj.poaAccuracy) > 0) {
			totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.poaAccuracy);
		}
		if(parseFloat(gradSheetObj.dxAccuracy) > 0) 
		{
			 totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.dxAccuracy);
		} 
		if(parseFloat(gradSheetObj.cptAccuracy) > 0) 
		{
			totalAccuracy = parseFloat(totalAccuracy) + parseFloat(gradSheetObj.cptAccuracy);
		} 
		
		return totalAccuracy;
	} 
     
     $scope.displayGradingSheet = function(drg){
    	 if(drg) {
    		 $scope.showRequired = false;
    		 $("#grading-table").hide();
    		 $("#accuracy-div").show();
    		 
    	 } else {
    		 $("#grading-table").show();
    		 $("#accuracy-div").hide();
    		 $scope.showRequired = true;
    	 }
     };

		
    
    $scope.captureWorkTime = function(){
    	$scope.queryData = {};
		$scope.queryData.taskId = $scope.$parent.selectedTaskID;
		$scope.queryData.userId = parseInt($rootScope.user.userId);
		$scope.queryData.workType = "coding";
		$http.post('web/coder/updateWorkStartTime', $scope.queryData).success(function(response) {
			console.log(response);
		});
	};
	
	//Capture the time, 
	//user spends on coding chart and store into DB.
    $scope.Cancel = function() {
    	ngDialog.openConfirm({
			template: 'app/coder/views/cancel_warning.html',
			className: 'ngdialog-theme-default'
		}).then(function (value) {
			$scope.captureWorkTime();
			//Enables user's menu links
			LinkHandlerService.enableCoderQAMenubarLinks();
	    	$location.path('/mytasks');
	    	
		}, function () {
			console.log('Modal promise rejected.');
			return;
		});
    	
	};
	
	//Capture the time, 
	//user spends on coding chart and store into DB.
	$scope.captureAuditWorkTime = function(){
    	$scope.queryData = {};
		$scope.queryData.taskId = $scope.$parent.selectedTaskID;
		$scope.queryData.userId = parseInt($rootScope.user.userId);
		$scope.queryData.workType = "globalAudit";
		$http.post('web/coder/updateWorkStartTime', $scope.queryData).success(function(response) {
			console.log(response);
		});
	};
	$scope.CancelAudit = function() {
		ngDialog.openConfirm({
			template: 'app/coder/views/cancel_warning.html',
			className: 'ngdialog-theme-default'
		}).then(function (value) {
			$scope.captureAuditWorkTime();
			//Enables user's menu links
			LinkHandlerService.enableCoderQAMenubarLinks();
			$scope.$parent.fromDate = $scope.$parent.fromDate;
			$scope.$parent.toDate = $scope.$parent.toDate;
			 
	    	$location.path('/audit');
		}, function () {
			console.log('Modal promise rejected.');
			return;
		});
	};
	
	$scope.insuranceOptions = [
	                           {name: 'Medicare', value: 'Medicare'},
	                           {name: 'Medicaid', value: 'Medicaid'},
	                           {name: 'Other', value: 'other'}
	                           ];
	
   $scope.displayInsuranceBox = function () {
	   if($scope.ipChart.insurance && ($scope.ipChart.insurance == 'other')){
		   $scope.insuranceOther = true;
		   $scope.insuranceOtherReq = true; 
	   } else {
		   $scope.insuranceOther = false;
		   $scope.insuranceOtherReq = false; 
	   }
   };
   
   $scope.displayChartHistory = false;
   $scope.chartHistory = {};
   // this function used to open the table to disply Coding Data History.
  $scope.viewCodingChartHistory = function(taskDetailData) {
	  $scope.chartHistory = JSON.parse(taskDetailData);
	  
	  $scope.displayChartHistory = true;
  };
  
  //init method to initialized the form on load.
  $scope.init = function ($location, $routeParams) {
  	console.log( " Initializing the TaskDetailController for  : " + $location.path());

  	if($rootScope.user) {
		if(($rootScope.user.userRole).indexOf("QA") > 0){
			$scope.userRole = "qa";
		} else if(($rootScope.user.userRole).indexOf("CODER") > 0) {
			$scope.userRole = "coder";
		}
		$scope.editTask();
  	} else if($routeParams && $routeParams.taskId != undefined){
    	$http.get('web/user/userdetails/').success(function(response) {
    		if (response.status == "OK") {
    			$rootScope.user = response.payLoad;
    			if($rootScope.user) {
    				$scope.editTask();
    			}
    		} else {
    			$location.path("/login");
    		}
		}).error(function(response) {
			console.log("Error Occurred");
        });
  	} else {
  		$location.path("mytasks");
  	}
  };
  
  $scope.init($location,$routeParams);   
  
};

