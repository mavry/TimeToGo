'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('NotifyCtrl',  function ($scope, $rootScope, $navigate, Backend) {	
  
  $scope.init = function() {
    $rootScope.data.notification = {
      request: {
        maxDrivingTime: $rootScope.data.route.drivingTime
      }
    };
  };

  (function() { $scope.init(); })();


  $scope.notifyMe = function() {
	  Backend.onNotify($rootScope.data.locations.startLocation.geoLocation, $rootScope.data.locations.destinationLocation.geoLocation, $rootScope.data.route.drivingTime);
  };

  $rootScope.doBack = function() { 
    $navigate.back()
  };

  // $rootScope.doBack = function() { 
  //   $location.path("/home");
  // }

  $scope.up = function() {
    $rootScope.data.notification.request.maxDrivingTime += 1;
  };

  $scope.down = function() {
    $rootScope.data.notification.request.maxDrivingTime -= 1;
  };
});