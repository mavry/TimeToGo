'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('GoCtrl',  function ($scope, $rootScope, Backend) {	
	
	$scope.init = function() {
		$scope.data = $rootScope.data;
		$scope.data.go =  $rootScope.data.go;
		$scope.data.go.eta = moment().add($scope.data.go.drivingTime).format("hh:mm");
	};

	$scope.openWaze = function() {
		Backend.openWaze($scope.wazeURL());
	};


	$scope.wazeURL = function() {
		var urlTemplate = "waze://?ll=%s,%s&navigate=yes&z=6";
		var url = sprintf(urlTemplate, 
				$scope.data.locations.destinationLocation.geoLocation.lat,
				$scope.data.locations.destinationLocation.geoLocation.lng);
		console.log("Waze URL "+url);
		return url;
	};

	(function() { $scope.init(); })();

});

