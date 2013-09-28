/*global angular, moment*/

angular.module('timeToGo.controllers'). controller('NotifyCtrl',  function ($scope, $rootScope, $navigate, $location, $timeout, Backend) {

  (function() {
    $scope.data.notification = {
      request: {
        maxDrivingTime: $rootScope.data.route.drivingTime,
    updatedAt: moment()
      }
    };
    $rootScope.data.notification = $scope.data.notification;
    $scope.data.liveInfo = {
      drivingTime: $rootScope.data.route.drivingTime,
      updatedAt: moment(),
      show: false
    };
  })();


  $scope.notifyMe = function() {
    Backend.onNotify($rootScope.data.locations.startLocation.geoLocation,
    $rootScope.data.locations.destinationLocation.geoLocation,
    $scope.data.notification.request.maxDrivingTime);
    $scope.intervalFunction();
  };


  $rootScope.doBack = function() {
    $navigate.back();
  };

  $scope.up = function() {
    $rootScope.data.notification.request.maxDrivingTime += 1;
  };

  $scope.down = function() {
    $rootScope.data.notification.request.maxDrivingTime -= 1;
  };

  $scope.getRequestTime = function() {
    return moment($scope.data.notification.request.updatedAt).format("H:mm");
  };

  $scope.getSinceRequestTime = function() {
    return moment().from($scope.data.notification.request.updatedAt, true);
  };

  $scope.getSinceLiveInfoUpdated = function() {
    return moment().from($scope.data.liveInfo.updatedAt, true);
  };


  $scope.intervalFunction = function(){
    $timeout(function() {
      $scope.data.liveInfo = Backend.getLiveInfo();
      $scope.data.liveInfo.show = true;
      if ($location.$$url.indexOf("/notify") >= 0 && !$scope.data.liveInfo.timeToGo) {
        $scope.intervalFunction();
      }
    }, 1000);
  };

});