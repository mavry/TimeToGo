'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('RouteCtrl',  function ($scope) {	
// this should be received from 'server'
	var route = {
		"road": "Road 1 South",
		"travelTime": 45
	};

	$scope.notify= {
		"request": {
			"road": route.road,
			"travelTime": route.travelTime
		}
	};

	$scope.up = function() {
		$scope.notify.request.travelTime += 1;
	};

	$scope.down = function() {
		$scope.notify.request.travelTime -= 1;
	};

});