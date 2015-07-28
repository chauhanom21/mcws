'use strict';

google.load('visualization', '1', {packages:['corechart']});

//google.setOnLoadCallback(function() {
//	  angular.bootstrap(document.body, ['myApp']);
//});

function SuperDashboardController($scope, $rootScope, $http, $filter, $location, SearchService) {
    $scope.$location = $location;
	
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
	
	
    
	$scope.loadChartData = function() {
		$http.get('web/supervisor/getDashboardData').success(function(response){
			if(response.status == 'OK'){
				$scope.workArray = response.payLoad.workArray;
				$scope.capacityWorkArray = response.payLoad.capacityArray;
				$scope.totalEM = response.payLoad.totalEM;
				$scope.totalCodersEM = response.payLoad.totalCodersEM;
				
				
			    $scope.name = 'Jay';
			    var datao = google.visualization.arrayToDataTable([
			                                                      	['Year', 'Sales', 'Expenses'],
															        ['2004', 1000, 400],
															        ['2005', 1170, 460],
															        ['2006', 660, 1120],
															        ['2007', 1030, 540]
			                                                    ]);
			    
			    var workJsonData = {
								'cols': [
								{"id":"","label":"Status","type":"string"},
								{"id":"","label":"ChartCount","type":"number"},
								],
								'rows': $scope.workArray
								};
			    
			   
			    // Create the data table.
                var workdata = new google.visualization.DataTable(workJsonData);
	    
			    var workOptions = { title: 'Total Work EM Uploaded  '+$scope.totalEM +" EM", 
			    				  	width:'500', 
			    				  	height:'400',
			    				  	colors: ['#04B431', '#FA5929', '#1C1C1C']};
			    var workChart = new google.visualization.PieChart(document.getElementById('totalWorkChartdiv'));
			    workChart.draw(workdata, workOptions);
			    
			    var coderWorkJsonData = {
						'cols': [
						{"id":"","label":"Status","type":"string"},
						{"id":"","label":"ChartCount","type":"number"},
						],
						'rows': $scope.capacityWorkArray
						};
			    
			    // Create the data table.
                var coderData = new google.visualization.DataTable(coderWorkJsonData);
	    
			    var coderOptions = {   title: 'Total EM Capacity Available  '+$scope.totalCodersEM +" EM", 
			    				  width:'500', 
			    				  height:'400',
			    				  colors: ['#04B431', '#FA5929', '#1C1C1C']};
			    var chart = new google.visualization.PieChart(document.getElementById('totalCapacityChartdiv'));
			    chart.draw(coderData, coderOptions);
			}
			
			
		});
	}
	
	$scope.xFunction = function() {
	      return function(d) {
	        return d.name;
	      };
	}
    $scope.yFunction = function() {
      return function(d) {
        return d.value;
      };
    }

    $scope.descriptionFunction = function() {
      return function(d) {
        return d.name;
      }
    }
    
    $scope.toolTipContentFunction = function(){
    	return function(key, x, y, e, graph) {
        	return  '<b>'+ key +'</b>' + ' : ' + x
    	}
    }
    
    var colorArray = ['#04B431', '#FE9A2E', '#FA5858'];
    $scope.colorFunction = function() {
    	return function(d, i) {
        	return colorArray[i];
        };
    }
};
