var coderModule = angular.module('coderModule', ['ngRoute', 'restangular', 'ngCookies', 'xeditable', 
                                                 'ui.bootstrap', 'ngDialog', 'timer', 'commonServiceModule']);

coderModule.config(function($routeProvider, $locationProvider, $httpProvider, RestangularProvider) {			

	$routeProvider.when('/mytasks', {
        templateUrl: 'web/coder/mytasks',
        controller: CoderController
    });	
	$routeProvider.when('/alltasks', {
        templateUrl: 'web/coder/alltasks',
        controller: CoderAllTasksController
    });	
	$routeProvider.when('/audit', {
        templateUrl: 'web/coder/audit',
        controller: AuditGradingController
    });
	$routeProvider.when('/coding_chart/:type/:taskId/:audit', {
        templateUrl: function(params) { return 'web/coder/coding_chart/'+params.type; },
        controller: TaskDetailsController
	});
	$routeProvider.when('/audit_chart/:type/:taskId/:audit', {
        templateUrl: function(params) { return 'web/coder/audit_chart/'+params.type; },
        controller: TaskDetailsController
	});
	$routeProvider.when('/login', {
        templateUrl: 'web/user/login',
        controller: CoderController
    });	
	//Route provider configuration for search page
	$routeProvider.when('/search', {
		templateUrl: 'web/coder/search',
		controller: SearchController
	});
	$routeProvider.when('/notes', {
		templateUrl: 'web/coder/notes',
		controller: CoderController
	});
	
	$routeProvider.otherwise({redirectTo: '/mytasks'});
		
	/* Register error provider that shows message on failed requests or redirects to login page on
	 * unauthenticated requests */
    $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
	        return {
	        	'responseError': function(rejection) {
	        		var status = rejection.status;
	        		var config = rejection.config;
	        		var method = config.method;
	        		var url = config.url;
	      
	        		if (status == 401 || status == 403) {
	        			$location.path( "/login" );
	        		} else {
	        			$rootScope.error = method + " on " + url + " failed with status " + status;
	        		}
	              
	        		return $q.reject(rejection);
	        	}
	        };
	    }
    );
});

//Login validation. 
//!!!!!! ATTENTION !!!!!!!

coderModule.run(function($rootScope, $location, $http) {
	
	if ($rootScope.user == undefined || $rootScope.user == null) {
		$http.get('web/user/userdetails').success(function(response) {
			if (response.status == "OK") {
				$rootScope.user = response.payLoad;
			} else {
				$location.path("/login");
			}
		}).error(function(response) {
			console.log("Error Occurred");
	    });
	} 
});


coderModule.service('LinkHandlerService',function ($filter) {	
	
    this.enableCoderQAMenubarLinks = function() {
    	$("#home").attr('href', "#home");
		$("#search").attr('href', "#search");
		$("#mytasks").attr('href', "#mytasks");
		$("#alltasks").attr('href', "#alltasks");
		$("#notes").attr('href', "#notes");
		$("#audit").attr('href', "#audit");
		$("#dailyaudit").attr('href', "#dailyaudit");
    };
    
    this.disableCoderQAMenubarLinks = function() {
    	$("#home").removeAttr('href', "#home");
		$("#search").removeAttr('href', "#search");
		$("#mytasks").removeAttr('href', "#mytask");
		$("#alltasks").removeAttr('href', "#alltask");
		$("#notes").removeAttr('href', "#notes");
		$("#audit").removeAttr('href', "#audit");
		$("#dailyaudit").removeAttr('href', "#dailyaudit");
    };
});

coderModule.directive('isNumber', function () {
	return {
		require: 'ngModel',
		link: function (scope) {	
			scope.$watch('gradSheet.pdxWeight', function(newValue,oldValue) {
                var arr = String(newValue).split("");
                if (arr.length === 0) return;
                if (arr.length === 1 && (arr[0] == '-' || arr[0] === '.' )) return;
                if (arr.length === 2 && newValue === '-.') return;
                if (isNaN(newValue)) {
                    scope.wks.number = oldValue;
                }
            });
		}
	};
});


coderModule.directive('validDecimalNumber', function() {
	  return {
		    require: '?ngModel',
		    link: function(scope, element, attrs, ngModelCtrl) {
		      if(!ngModelCtrl) {
		        return; 
		      }

		      ngModelCtrl.$parsers.push(function(val) {
		    	if(val) {
		    		//ng-pattern="/^[0-9]{1,7}$/"
		    		var clean = val.replace( /[^0-9.]$/, '');
			        if (val !== clean) {
			          ngModelCtrl.$setViewValue(clean);
			          ngModelCtrl.$render();
			        }
			        return clean;
		    	}  
		        
		      });

		      element.bind('keypress', function(event) {
		        if(event.keyCode === 32) {
		          event.preventDefault();
		        }
		      });
		    }
		  };
});

coderModule.directive('validNumber', function() {
	  return {
		    require: '?ngModel',
		    link: function(scope, element, attrs, ngModelCtrl) {
		      if(!ngModelCtrl) {
		        return; 
		      }

		      ngModelCtrl.$parsers.push(function(val) {
		    	if(val) {
		    		//ng-pattern="/^[0-9]{1,7}$/"
		    		var clean = val.replace( /[^0-9]$/, '');
			        if (val !== clean) {
			          ngModelCtrl.$setViewValue(clean);
			          ngModelCtrl.$render();
			        }
			        return clean;
		    	}  
		        
		      });

		      element.bind('keypress', function(event) {
		        if(event.keyCode === 32) {
		          event.preventDefault();
		        }
		      });
		    }
		  };
});

coderModule.service('SortService', function () {
	
	this.sortWorkItemById = function(array, key) {
		console.log("Calling sortWorkItemById ")
        return array.sort(function(a, b) {
            var x = (a[key]); 
            var y = (b[key]);
            return ((y < x) ? -1 : ((y > x) ? 1 : 0));
        });
    }
	
	this.sortWorkItemByUpdateDateDESC = function(array, key) {
		console.log("Calling sortWorkItemByUpdateDateDESC ")
        return array.sort(function(a, b) {
            var x = (a[key]); 
            var y = (b[key]);
            return ((y < x) ? -1 : ((y > x) ? 1 : 0));
        });
    }
	
    this.sortByKeyAsc = function(array, key) {
        return array.sort(function(a, b) {
            var x = (a[key].trim()).toLowerCase(); 
            var y = (b[key].trim()).toLowerCase();
            return ((x < y) ? -1 : ((x > y) ? 1 : 0));
        });
    }
    this.sortByKeyDesc = function(array, key) {
        return array.sort(function(a, b) {
        	var x = (a[key].trim()).toLowerCase(); 
            var y = (b[key].trim()).toLowerCase();
            return ((y < x) ? -1 : ((y > x) ? 1 : 0));
        });
    }
});


coderModule.service('CoderFormService',function ($filter) {
	 this.addNewRow = function (name, array, index) {
	     array.splice(index+1, 0, {'name':  ""+ name +""+ array.length});
     };
     
	 this.removeRow = function (array, index) {
		if(array.length == 1) {
			 alert("No more textbox to remove.");
             return false;
		}
		console.log("Index")
		array.splice(index, 1);
	 };
		
     this.populateMutliValueFormFields = function (array, obj) {
    	 if(obj && array) {
    		 for(var i = 0; i < obj.length ; i++) {
    			 if(obj[i].value == undefined) {
    				 array.push({'name':""+ obj[i].name, 'value':""+""});
    			 }  else {
    				 array.push({'name':""+ obj[i].name, 'value':""+obj[i].value});
    			 } 				 
    		 }
    	}
    }
     
    this.populateGroupFormFields = function (array, obj) {
    	 if(obj && obj.length > 0 && array) {
    		 for(var i = 0; i < obj.length ; i++) {
    			array.push(obj[i]);
    		 }
    	} else {
    		array.push({ "icd":[{'name':'icd'}], 
				 "cpt":[{'name':'cpt'}], 
				 "mod":[{'name':'mod'}]
			  });
    	}
   }
    
    this.populateRCGroupFormFields = function (array, obj) {
   	 if(obj && obj.length > 0 && array) {
   		 for(var i = 0; i < obj.length ; i++) {
   			array.push(obj[i]);
   		 }
   	} else {
   		array.push({ "revenueCode":[{'name':'revenueCode'}],
				     "dos":[{'name':'dos'}],
				     "phy":[{'name':'phy'}]
			      });
   	}
  }
    
   this.populateIpIcdGroupFormFields = function (array, obj) {
   	 if(obj && obj.length > 0 && array) {
   		 for(var i = 0; i < obj.length ; i++) {
   			array.push(obj[i]);
   		 }
   	} else {
   		array.push([{ "icd":[{'name':'icd'}], 
			"dos":[{'name':'dos'}],
			"phy":[{'name':'phy'}]
		  }]);
   	}
  }
   
  this.populateGECptGroupFormFields = function (array, obj) {
   	 if(obj && obj.length > 0 && array) {
   		 for(var i = 0; i < obj.length ; i++) {
   			array.push(obj[i]);
   		 }
   	} else {
   		array.push([{ "cpt":[{'name':'cpt'}], 
   			"mod":[{'name':'mod'}],
   			"dx":[{'name':'dx'}]
		  }]);
   	}
  }
  
  this.populateSdxpoaGroupFormFields = function (array, obj) {
	  if(obj && obj.length > 0 && array) {
	   		 for(var i = 0; i < obj.length ; i++) {
	   			array.push(obj[i]);
	   		 }
   	  } else {
   		array.push([{ "sdx":[{'name':'sdx'}], 
   			"sdx_poa":[{'name':'sdx_poa'}]
		  }]);
   	 }
  }
    
   var getType = function (p) {
   	    if (Array.isArray(p)) return 'array';
	    else if (typeof p == 'string') return 'string';
	    else if (p != null && typeof p == 'object') return 'object';
	    else return 'other';
   }
	
     
	this.populateSingleValueFormFields = function (objArray, codingChart) {
		if(objArray){
			codingChart.status = objArray.status;
			codingChart.comments = objArray.comments;
			codingChart.specialty = objArray.specialty;
			codingChart.patientName = objArray.patientName;
			codingChart.patientDOB = objArray.patientDOB;
			codingChart.admitDate = objArray.admitDate;
			codingChart.disDate = objArray.disDate;
			codingChart.geEncounter = objArray.geEncounter;
			codingChart.admit = objArray.admit;
			codingChart.dc = objArray.dc;
			codingChart.barea = objArray.barea;
			codingChart.division = objArray.division;
			codingChart.loc = objArray.loc;
			codingChart.io = objArray.io;
			codingChart.md = objArray.md;
			codingChart.extender = objArray.extender;
			codingChart.refmd = objArray.refmd;
			codingChart.docName = objArray.docName;
			codingChart.affinityCode = objArray.affinityCode;
			codingChart.mdCpt = objArray.mdCpt;
			codingChart.upCode = objArray.upCode;
			codingChart.downCode = objArray.downCode;
			codingChart.units = objArray.units;
			codingChart.sdx2 = objArray.sdx2;
			codingChart.sdx3 = objArray.sdx3;
			codingChart.sdx4 = objArray.sdx4;
			codingChart.sdx5 = objArray.sdx5;
			codingChart.mrn = objArray.mrn;
			codingChart.adx = objArray.adx;
			codingChart.pdx = objArray.pdx;
			codingChart.pdxpoa = objArray.pdxpoa;
			codingChart.drg = objArray.drg;
			codingChart.disp = objArray.disp;
			codingChart.mcc = objArray.mcc;
			codingChart.insurance = objArray.insurance;
			codingChart.insuranceOther = objArray.insuranceOther;
			codingChart.affinityExportDate = objArray.affinityExportDate;
			codingChart.codingComents = objArray.codingComents;
		}
	}
	
	
     
    this.calculateAccuracy = function (weightage, error, codes, colName, gradSheetObj) {
    	if(colName == 'adx') {
    		if(codes >0)
    			gradSheetObj.adxAccuracy = ((weightage / codes)*error).toFixed(2);
    		else
    			gradSheetObj.adxAccuracy = 0;
			gradSheetObj.grandAccuracy = gradSheetObj.adxAccuracy;
			
		} else if(colName == 'pdx') {
			if(codes > 0)
				gradSheetObj.pdxAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.pdxAccuracy = 0;
			
    		gradSheetObj.grandAccuracy = gradSheetObj.pdxAccuracy;	
    		
		} else if(colName == 'sdx') {
			 if(codes > 0)
				 gradSheetObj.sdxAccuracy = ((weightage / codes)*error).toFixed(2);
			 else
				 gradSheetObj.sdxAccuracy = 0;
			 
			 gradSheetObj.grandAccuracy = gradSheetObj.sdxAccuracy;
			 
		} else if(colName == 'mcc') {
			 if(codes > 0)
				 gradSheetObj.mccAccuracy = ((weightage / codes)*error).toFixed(2);
			 else
				 gradSheetObj.mccAccuracy = 0;
			 
			 gradSheetObj.grandAccuracy = gradSheetObj.mccAccuracy;
			 
		} else if(colName == 'icd') {
			 if(codes > 0)
				 gradSheetObj.icdAccuracy = ((weightage / codes)*error).toFixed(2);
			 else 
				 gradSheetObj.icdAccuracy = 0;
			 
			 gradSheetObj.grandAccuracy = gradSheetObj.icdAccuracy;
			 
		} else if(colName == 'drg') {
			if(codes > 0)
				gradSheetObj.drgAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.drgAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.drgAccuracy;
			
		} else if(colName == 'disp') {
			if(codes > 0) 
				gradSheetObj.dispAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.dispAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.dispAccuracy;
			
		} else if(colName == 'dos') {
			if(codes > 0) 
				gradSheetObj.dosAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.dosAccuracy = 0
			
				gradSheetObj.grandAccuracy = gradSheetObj.dosAccuracy;
			
		} else if(colName == 'physician') {
			if(codes > 0) 
				gradSheetObj.physicianAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.physicianAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.physicianAccuracy;
			
		} else if(colName == 'revenueCode') {
			if(codes > 0) 
				gradSheetObj.revenueCodeAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.revenueCodeAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.revenueCodeAccuracy;
			
		} else if(colName == 'mod') {
			if(codes > 0) 
				gradSheetObj.modAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.modAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.modAccuracy;
			
		} else if(colName == 'poa') {
			if(codes > 0) 
				gradSheetObj.poaAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.poaAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.poaAccuracy;
			
		} else if(colName == 'cpt') {
			if(codes > 0) 
				gradSheetObj.cptAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.cptAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.cptAccuracy;
			
		} else if(colName == 'dx') {
			if(codes > 0) 
				gradSheetObj.dxAccuracy = ((weightage / codes)*error).toFixed(2);
			else
				gradSheetObj.dxAccuracy = 0;
			
			gradSheetObj.grandAccuracy = gradSheetObj.dxAccuracy;
			
		} 
	} 
});
