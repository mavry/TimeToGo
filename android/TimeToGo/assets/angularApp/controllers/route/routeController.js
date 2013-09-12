'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('RouteCtrl',  function ($scope, $rootScope, $location, Backend) {	

  $scope.notifyMe = function() {
	Backend.onNotify($scope.maxDrivingTime);
  };

  Backend.onGo($rootScope.data.startLocation.geoLocation.lat, $rootScope.data.startLocation.geoLocation.lng, 
	$rootScope.data.destinationLocation.geoLocation.lat, $rootScope.data.destinationLocation.geoLocation.lng, 
	function(time, name, lastUpdated) {
  	  $scope.maxDrivingTime= time;
	  $scope.roadName = name;
	}
  );

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