'use strict';

/**
 * 
 * @param $scope
 * @param $rootScope
 * @param $http
 * @param $filter
 * @param $routeParams
 * @param $location
 */
function SearchController($scope, $rootScope, $http, $filter, $modal, $routeParams, $location, ngDialog) {
    $scope.$location = $location;
    $scope.filteredItems = [];
    $scope.itemsPerPage = 5;  //Do not change this value.If change here then change in "ui-bootstrap-tpls-0.10.0.js" also.
    $scope.pagedItems = [];
    $scope.currentPage = 0;
	$scope.items = [];
	$scope.disableSearch = false;
	
	$scope.searchTaskDetail = function () {
		$http.get("web/search/charts?searchQuery="+$scope.searchQuery).success(function(response) {
			if (response.status == 'ERROR' || response.payLoad == null){
				$scope.openErrorMessage();
				$scope.items = [];
			} else {
				$scope.items = response.payLoad;
			}
			return;
		}).error(function(response) {
			$scope.openErrorMessage();
			$scope.items = [];
        });
	};
	
	$scope.openErrorMessage = function () {
		var dialog = ngDialog.open({
			template: '<p>No Record Found, Insert Correct Account/MR Number!</p>',
			className: 'ngdialog-error-theme-plain',
			plain: true
		});
		dialog.closePromise.then(function (data) {
			$scope.searchQuery='';
			$scope.disableSearch = false;
		});
		$scope.disableSearch = true;
	};
	
	 // this function used to open the table to display Coding Data History.
	 $scope.viewCodingChartHistory = function(codingData, chartType, status) {
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
					 } else if(chartType == "EKG_ECG") {
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
