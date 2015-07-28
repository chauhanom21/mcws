'use strict';

pointyApp.factory('UserService',['Restangular', function (Restangular) {
	
    return {
        save: function(user) {
        	var basePatients = Restangular.all("users");
        	//var aPromise = basePatients.post(user);
            var aPromise = "success";
        	return aPromise;
        }
    };

}]);
