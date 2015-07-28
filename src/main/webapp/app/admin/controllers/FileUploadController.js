var workListUploadApp = angular.module('WorkListUploadApp', ['angularFileUpload']);
 
var FileUploadController =  function($scope, $http) {

    $scope.document = {};    

	$scope.uploadFile = function() {
		alert('File submitted...');
		var formData = new FormData();
		formData.append("file", file.files[0]);
		$http({
			method : 'POST',
			url : 'web/admin/upload',
			headers : {
				'Content-Type' : 'undefined'
			},
			data : formData, 
			transformRequest: function(data, headersGetterFunction) {
		        return data; // do nothing! FormData is very good!
		    }
		}).success(function(response, status) {
			$scope.grantRecordsRef = response.payLoad;
			$scope.message = response.message;
			if (response.status == 'ERROR') {
				$scope.alerts.push({type: 'danger', msg: response.message});
				$scope.showAlert = true;
				$scope.canSaveGrants = false;
			} else {
				$scope.alerts.push({msg: response.message});
				$scope.showAlert = true;
				$scope.canSaveGrants = true;
			}
		}).error(function(data, status) {
//			alert(JSON.stringify(status));
		    $scope.alerts.push({type: 'danger', msg: "Error during Import: " + status});
			$scope.showAlert = true;
			return;
		});
	};
};