'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('RouteCtrl',  function ($scope, $rootScope, $location, Backend) {	
	//WazeTravelTimeService.getRoute($rootScope.data.startLocation, $rootScope.data.destinationLocation, function(route) {
	$rootScope.routInfo = function(time, name, lastUpdated) {
		$scope.maxDrivingTime= time;
		$scope.roadName = name;
	}

	$scope.notifyMe = function() {
		Backend.onNotify($scope.maxDrivingTime);
	};

	Backend.onGo($rootScope.data.startLocation.geoLocation.lat, $rootScope.data.startLocation.geoLocation.lng, 
		$rootScope.data.destinationLocation.geoLocation.lat, $rootScope.data.destinationLocation.geoLocation.lng, 
		$rootScope.routInfo);


	$rootScope.doBack = function() { 
		$location.path("/home");
	}

	$scope.up = function() {
		$scope.maxDrivingTime += 1;
	};

	$scope.down = function() {
		$scope.maxDrivingTime -= 1;
	};

});