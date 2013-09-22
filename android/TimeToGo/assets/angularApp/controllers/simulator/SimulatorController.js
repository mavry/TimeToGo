 'use strict';

/* Controllers */

angular.module('timeToGo.controllers.mock'). controller('SimulatorCtrl',  function ($rootScope, $scope, localStorageService, simulatorService) {	

 $rootScope.mockData = localStorageService.get("mock") || {
    drivingTime: 51,
    routeName: "road 77",
    currentLocation: {
      lat: 32.6467854,
      lng: 34.9891009
    }
  };

  $rootScope.$watch('mockData', function(newVal, oldVal) {
    $rootScope.mockData = newVal;
    $rootScope.mockData.drivingTime=parseInt($rootScope.mockData.drivingTime);
    localStorageService.add("mock", $rootScope.mockData);

  }, true);



  $scope.onCreate = function() {
    // fromAndroaid.onCreate();
  };
  $scope.onStart = function() {
    // fromAndroaid.onStart();
  };
  $scope.onResume = function() {
    // fromAndroaid.onResume();
  };
  $scope.onPause = function() {
    // fromAndroaid.onPause();
  };
  $scope.updateUI = function() {
    // updateUI();
  };
  $scope.onTimeToGo = function() {
    // fromAndroaid.onTimeToGo();
  };
  $scope.onDrivingTime = function() {
    // fromAndroaid.onDrivingTime();
  };
  $scope.onCurrentLocation = function(lat, lng) {
    simulatorService.onCurrentLocation(mockData.currentLocation.lat,mockData.currentLocation.lng);
  };

 
});


