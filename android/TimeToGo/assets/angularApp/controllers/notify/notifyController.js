'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('NotifyCtrl',  function ($scope, $rootScope, $navigate, Backend) {	
  
  (function() {
    $scope.data.notification = {
      request: {
        maxDrivingTime: $rootScope.data.route.drivingTime,
        updatedAt: moment()
      }
    };
    $scope.data.liveInfo = {
      drivingTime: $rootScope.data.route.drivingTime,
      updatedAt: moment(),
      show: false
    }
  })();


  $scope.notifyMe = function() {
	  // Backend.onNotify($rootScope.data.locations.startLocation.geoLocation, 
   //    $rootScope.data.locations.destinationLocation.geoLocation, 
   //    $scope.data.notification.request.maxDrivingTime);
    $scope.data.liveInfo.show = true;
    $scope.data.liveInfo.updatedAt = moment();
  };

  $rootScope.doBack = function() { 
    $navigate.back()
  };

  $scope.up = function() {
    $rootScope.data.notification.request.maxDrivingTime += 1;
  };

  $scope.down = function() {
    $rootScope.data.notification.request.maxDrivingTime -= 1;
  };

  $scope.getRequestTime = function() {
    moment($scope.data.notification.request.updatedAt).format("hh:mm");
  };

  $scope.getSinceRequestTime = function() {
    moment().diff($scope.data.notification.request.updatedAt).format("minutes");
  };

});