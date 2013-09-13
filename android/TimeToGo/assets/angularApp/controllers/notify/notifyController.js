'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('NotifyCtrl',  function ($scope, $rootScope, $location, Backend) {	
  
  $scope.init = function() {
    $rootScope.data.notification = {
      request: {
        maxDrivingTime: $rootScope.data.route.drivingTime
      }
    };
  };

  (function() { $scope.init(); })();


  $scope.notifyMe = function() {
	  Backend.onNotify($rootScope.data.route.drivingTime);
  };

  $rootScope.doBack = function() { 
    $location.path("/home");
  }

  $scope.up = function() {
    $rootScope.data.notification.request.maxDrivingTime += 1;
  };

  $scope.down = function() {
    $rootScope.data.notification.request.maxDrivingTime -= 1;
  };
});