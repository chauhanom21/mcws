'use strict';

/**
 * UserConfigController
 * @constructor
 */
var UserConfigController = function($scope, $http, $location, $routeParams, $filter, AdminSearchService) {
	$scope.$location = $location;
    $scope.alerts = [];
    $scope.userDetails = {};
    $scope.userslist = [];
    $scope.clients = [];
    $scope.allChartSpeDetail = [];
    $scope.unMapClients = [];
    $scope.selectedClients = [];
    $scope.chartSpeDetails = [];
    $scope.selectedChartSpes = [];
    $scope.finalClientList = [];
    $scope.finalChartList = [];
    $scope.allChartTypes = [];
    
    $scope.specialization = "";
    $scope.chartType ="";
    
    $scope.listbox_moveacross =  function (sourceID, destID) {
        var src = document.getElementById(sourceID);
        var dest = document.getElementById(destID);

        var selIndex = src.selectedIndex;
      
        if(-1 == selIndex) {
            alert("Please select an option to move.");
            return false;
        }
        
        for(var count=0; count < src.options.length; count++) {

            if(src.options[count].selected == true) {
                var option = src.options[count];

                var newOption = document.createElement("option");
                newOption.value = option.value;
                newOption.text = option.text;
                newOption.selected = true;
                try {
                    dest.add(newOption, null); //Standard
                    src.remove(count, null);
                } catch(error) {
                    dest.add(newOption); // IE only
                    src.remove(count);
                }
                count--;
            } 
        }
    }
    
   $scope.listbox_selectall =  function(listID, isSelect) {
	    $scope.finalClientList = [];
	    $scope.finalChartList = [];
	    
        var listbox = document.getElementById(listID);
        
        if(listID == 'selectedClients') {
        	for(var count=0; count < listbox.options.length; count++) {
                listbox.options[count].selected = isSelect;
              
                for(var i=0; i < $scope.clients.length; i++) {
                	if(listbox.options[count].text == $scope.clients[i].name) {
                		$scope.finalClientList.push($scope.clients[i]);
                	}
                } 
            }
        }
        
        if(listID == 'selectedCharts') {
        	for(var count=0; count < listbox.options.length; count++) {
                listbox.options[count].selected = isSelect;
              
                for(var i=0; i < $scope.allChartSpeDetail.length; i++) {
                	if(listbox.options[count].text == $scope.allChartSpeDetail[i].name) {
                		$scope.finalChartList.push($scope.allChartSpeDetail[i]);
                	}
                } 
            }
        }
        
        
    };
       
    /**
     *  Load all Users data
     */
    $scope.loadAllUser = function() {
    	
    	//Hide the link of User cleint Configuration until user not selected
    	$("#userClientConfigLink").hide();
    	
    	$http.get('web/config/user/loadAllUsers').success(function (response) {
    		if(response.status == 'OK') {
    			$scope.userslist = response.payLoad;
            } else {
            	alert("Unable to load Users !!!");
            	return;
            }
        });	    
    };
    
	/**
	 * Load the selected user's details
	 */
    $scope.getUserDetails = function(loadClientData){
    	//display the link of User client Configuration After user got selected
    	$("#userClientConfigLink").show();
    	
    	
    	$scope.userDetails = {};
    	$scope.loadClientDetail = false;
    	if(loadClientData) {
    		$scope.loadClientDetail = true;
    	}  
    	if( $scope.userSelected ) {
	    	$http.get('web/config/user/loadUserDetails/'+$scope.userSelected+"/"+$scope.loadClientDetail).success(function (response) {
	    		if(response.status == 'OK') {
	    			
	    			$scope.userDetails = response.payLoad;
	    			/*
	    			 * Client Details use for user client configuration
	    			 */
	    			if($scope.userDetails.clientDetails && $scope.userDetails.clientDetails.length > 0){
	    				$scope.selectedClients = $scope.userDetails.clientDetails;
	    			} 
	    			
	    			/**
	    			 * User's already mapped chart specialization
	    			 */
	    			if($scope.userDetails.chartSpeDetails && $scope.userDetails.chartSpeDetails.length > 0){
	    				$scope.selectedChartSpes = $scope.userDetails.chartSpeDetails;
	    			} 
	    			
	            } else {
	            	alert("Unable to load User Details!!!");
	            }
	        });	   
    	} 
    }
    
    /**
     * Add new user or update the existing user details.
     */
    $scope.saveOrUpdateUserDetails = function() {
    	
    	if($scope.userSelected) {
    		$http.put('web/config/user/update/',$scope.userDetails).success(function (response) {
	    		if(response.status == 'OK') {
	    			$scope.userDetails = response.payLoad;
	    			
	    			alert(" Successfully Updated User Details !!!");
	    			
	    			/**
	    	    	 * After done with Updating the user, Loading fresh data from database.
	    	    	 */
	    	    	$scope.loadAllUser();
	    	    	
					$scope.userSelected = "";
	    			return;
	            } else {
	            	alert(" Form submittion failed !!!");
	            	return;
	            }
	        });	   
    	} else {
    		$http.post('web/config/user/add/',$scope.userDetails).success(function (response) {
	    		if(response.status == 'OK') {
	    			$scope.userDetails = response.payLoad;
	    			alert(" Successfully Added New User Details !!!");
	    			
	    			/**
	    	    	 * After done with Adding the user, Loading fresh data from database.
	    	    	 */
	    	    	$scope.loadAllUser();
	    	    	$scope.userSelected = "";
	    			return;
	            } else {
	            	alert(" Failed to update selected user's details!!!");
	            	return;
	            }
	        });	   
    	}
    	
    	
    };
    
    $scope.saveChartSpecialization = function(){
    	$scope.chartSpeData = {};
    	$scope.chartSpeData.chartType = $scope.chartType;
    	$scope.chartSpeData.chartSpelization = $scope.specialization;
    	
    	$http.post('web/config/client/saveChartSpecialization', $scope.chartSpeData).success(function (response) {
    		if(response.status == 'OK') {
    			alert(" Successfully Added New ChartSpecialization Details !!!");    			  	    	
    			return;
            } else {
            	console.log(response.message);
            	//alert("Failed to Add ChartSpecialization details!!!");
            	alert(response.message);
            	return;
            }
        });	   
    	
    };
    
    $scope.validateUserRole = function(){
    	if(	$scope.userDetails) {
    		if(	$scope.userDetails.coder || $scope.userDetails.localQA ) {
    			if( $scope.userDetails.localQA ) {
    				$scope.userDetails.remoteQA = false;
    			} else {
    				$scope.userDetails.localQA = false;
    			}
    			
    			if($scope.userDetails.newCoder) {
    				$scope.userDetails.localQA = false;
    			} else {
    				$scope.userDetails.newCoder = false;
    			}
    			
    			$scope.userDetails.remoteQA = false;
    		} else if(	$scope.userDetails.remoteQA ) {
    			$scope.userDetails.newCoder = false;
    			$scope.userDetails.coder = false;
    			$scope.userDetails.localQA = false;
    		} else {
    			alert("Please select user Role !!!");
    			return;
    		}
    	}
    };
    
	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};	
	
	
	
	$scope.loadAllClients = function() {
		$http.get('web/client/detail/allClientsDetail').success(function(data) {
			$scope.clients = data.payLoad;
		});
	};
	
	$scope.loadAllChartSpeDetail = function() {
		$http.get('web/config/client/allChartSpeDetail').success(function(data) {
			$scope.allChartSpeDetail = data.payLoad;
		});
	};
	
	$scope.loadAllChartTypes = function() {
		$http.get('web/config/client/allChartTypes').success(function(data) {
			$scope.allChartTypes = data.payLoad;
		});
	};
	
	$scope.chartTypeDisabled = false;
	$scope.populateChartType = function() {
		if($scope.chartSelected) {
			$scope.chartType = $scope.chartSelected.chartType;
			$scope.chartTypeDisabled = true;
		}
		if($scope.chartSelected === null) {
			$scope.chartType = "";
			$scope.chartTypeDisabled = false;
		}
	};
	
	$scope.loadAlDataForChartSplCreate = function() {
		
		$scope.loadAllChartSpeDetail(); 		
		$scope.loadAllChartTypes();
	};
	/**
	 * This function constructs the all client details which are not mapped with user.
	 */
	$scope.loadUserNotMapClients = function() {
		$scope.unMapClients = [];
		$http.get('web/config/client/loadUserNotMapClients/'+$scope.userSelected).success(function(data) {
			$scope.unMapClients = data.payLoad;
		});
		
	};
	
	$scope.loadUserNotMapChartSpeDetails = function() {
		$scope.chartSpeDetails = [];
		$http.get('web/config/client/loadUserNotMapChartSpeDetails/'+$scope.userSelected).success(function(data) {
			$scope.chartSpeDetails = data.payLoad;
		});
	};
	
   
	$scope.loadRequireUserData = function() {
		if ($routeParams != undefined && $routeParams.userId != undefined) {
			$scope.userSelected = $routeParams.userId;
			
			//Load All Client Details
			$scope.loadAllClients();
			
			//Load All chart Types
			$scope.loadAllChartSpeDetail();
			
			//Load user details along with mapped clients and chart specialization
			$scope.getUserDetails(true);
			
			// Loads All clients details which are not mapped with selected user
			$scope.loadUserNotMapClients();
			
			// Loads All Chart Specialization which are not mapped with selected user
			$scope.loadUserNotMapChartSpeDetails();
						
		}
	}
	
	/**
	 * Submit the User's Client Mapping Details Form
	 */
	$scope.submitUserClientConfigForm = function () {
		$scope.listbox_selectall('selectedClients', true);
		
		if($scope.finalClientList.length > 0) {
			
			$scope.requestData = {};
			$scope.requestData.clientIds = [];
			$scope.requestData.userId = $scope.userSelected;
			
			for(var i=0;i < $scope.finalClientList.length ; i++) {
				$scope.requestData.clientIds.push($scope.finalClientList[i].id);
			}
		
			$http.post('web/config/user/saveUserClientDetails',$scope.requestData).success(function(response) {
				if(response.status == 'OK') {
					alert("Successfully Updated User's client Configuration details !!!!")
					return;
				} else {
					alert("Unable to Submit the Form, Got Error While updating User details !!!!")
					return;
				}
				$scope.chartSpeDetails = data.payLoad;
			});
		} else {
			alert(" Select User's client !!");
			return;
		}
		
	}
	
	/**
	 * Submit the User's chart specialization Mapping details form.
	 */
	$scope.submitUserChartSpeConfigForm = function () {
		$scope.listbox_selectall('selectedCharts', true);
		
		if($scope.finalChartList.length > 0) {
			
			$scope.requestData = {};
			$scope.requestData.chartSpeIds = [];
			$scope.requestData.userId = $scope.userSelected;
			
			for(var i=0;i < $scope.finalChartList.length ; i++) {
				$scope.requestData.chartSpeIds.push($scope.finalChartList[i].id);
			}
			
			$http.post('web/config/user/saveUserChartSpecDetails',$scope.requestData).success(function(response) {
				if(response.status == 'OK') {
					alert(response.message);
					return;
				} else {
					alert(response.message);
					return;
				}
				$scope.chartSpeDetails = data.payLoad;
			});
			
		} else {
			alert(" Select Chart Specialization !! ");
			return;
		}
	}
	
};

/**
 * 
 * @param listID
 * @param direction
 * @returns {Boolean}
 */
function listbox_moveupdown(listID, direction) {

    var listbox = document.getElementById(listID);
    var selIndex = listbox.selectedIndex;
    
    if(-1 == selIndex) {
        alert("Please select an option to move.");
        return false;
    }
    
    var increment = -1;
    if(direction == 'up') {
    	if(selIndex == 0) {
	    	alert('First element cannot be moved up');
	    	return false;
	    } else {
	    	increment = -1;
	    }
    }  else {
    	if(listbox.selectedIndex == listbox.options.length-1) {
    		alert('Last element cannot be moved down'); 
    		return false;
    	} 		    	
    	else
    		increment = 1;	
    }

    if((selIndex + increment) < 0 || (selIndex + increment) > (listbox.options.length-1)) {
        return;
    }

    var selValue = listbox.options[selIndex].value;
    var selText = listbox.options[selIndex].text;
    listbox.options[selIndex].value = listbox.options[selIndex + increment].value
    listbox.options[selIndex].text = listbox.options[selIndex + increment].text

    listbox.options[selIndex + increment].value = selValue;
    listbox.options[selIndex + increment].text = selText;

    listbox.selectedIndex = selIndex + increment;
}
