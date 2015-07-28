var commonModule = angular.module('commonServiceModule', []);

commonModule.service('SortService', function () {
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

commonModule.service('SearchService',function ($filter) {	
	
	this.searchByName = function(searchItems, queryName) {
        return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
                if (searchMatch(item['name'], queryName)) {
                    return true;
                }
            }
            return false;
        });

    };
	
	this.searchByRole = function(searchItems, queryRole) {
        return filteredItemsRole = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
                if (searchMatch(item['role'], queryRole)) {
                    return true;
                }
            }
            return false;
        });

    };

	this.searchByClient = function(searchItems, queryClient) {
        return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
                if (searchMatch(item['client'], queryClient)) {
                    return true;
                }
            }
            return false;
        });

    };
	this.searchByStatus = function(searchItems, queryStatus) {
	    return filteredNameItems = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['status'], queryStatus)) {
                    return true;
                }
            }
            return false;
        });

    };
	
    this.searchByChartType = function(searchItems, queryChartType) {
        return filteredItemsRole = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['chartType'], queryChartType)) {
                    return true;
                }
            }
            return false;
        });

    };
    
    this.searchByChartSpecialization = function(searchItems, queryChartSpl) {
        return filteredItemsRole = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatchSpeciazation(item['specilization'], queryChartSpl)) {
                    return true;
                }
            }
            return false;
        });

    };
	
    this.searchByStatusType = function(searchItems, queryStatusType) {
        return filteredItemsStatus = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['status'], queryStatusType)) {
                    return true;
                }
            }
            return false;
        });

    };
    
    this.searchByDRG = function(searchItems, queryDRG) {
        return filteredItemsStatus = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['drg'], queryDRG)) {
                    return true;
                }
            }
            return false;
        });

    };
    
    this.searchByLocation = function(searchItems, queryDRG) {
        return filteredItemsStatus = $filter('filter')(searchItems, function (item) {
            for(var attr in item) {
			    if (searchMatch(item['location'], queryDRG)) {
                    return true;
                }
            }
            return false;
        });

    };
    
    this.search = function(items, query){
            return searchItems = $filter('filter')(items, function (item) {
                for(var attr in item) {
                    if (searchMatch(item[attr], query)) {
                        return true;
                    }
                }
                return false;
            });
    };

    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
		if(haystack != null) {
			if (typeof haystack == 'string' || haystack instanceof String) {
				return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
			}
			else {
				var haystackStr = haystack.toString();
				return haystackStr.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
			}
		} else {
			return false;
		}
    };
    
    var searchMatchSpeciazation = function (haystack, needle) {
        if (!needle) {
            return true;
        }
		if(haystack != null) {
			if (typeof haystack == 'string' || haystack instanceof String) {
				if(haystack.toLowerCase() == needle.toLowerCase()){
					return true
				}
				return false;
			}
			else {
				var haystackStr = haystack.toString();
				if(haystackStr.toLowerCase() == needle.toLowerCase()){
					return true
				}
				return false;
			}
		} else {
			return false;
		}

    };
    
});

commonModule.service('DataService', function ($http) {
	console.log("==== Data Service")
	this.clients = function() {
		console.log("==== Data Service clients")
		$http.get('web/client/detail/all').success(function(data) {
			return data.payLoad;
		});
	};
	this.chartTypes = function() {
		$http.get('web/client/detail/allChartTypes').success(function(data) {
			return data.payLoad;
		});
	};
	this.chartSpecialization = function() {
		$http.get('web/supervisor/getAllUniqueChartSpecialization').success(function(data){
			return data.payLoad;
		});
	};
	this.chartStatus = function() {
		$http.get('app/clientconfig.properties').then(function (response) {
			if(response.data)
			{
				return response.data.monitorTaskChartStatus;
			} 
		});
	}
});
