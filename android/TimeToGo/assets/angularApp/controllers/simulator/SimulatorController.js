 'use strict';

/* Controllers */

angular.module('timeToGo.controllers.mock'). controller('SimulatorCtrl',  function ($rootScope, $scope, localStorageService, simulatorService) {	

 var mockData = localStorageService.get("timeToGo.mock") || {
    travelTime: 51,
    roadName: "road 77",
    currentLocation: {
      lat: 32.6467854,
      lng: 34.9891009
    }
  };

  localStorageService.add("timeToGo.mock", mockData);
  $rootScope.mockData = mockData; 

  $rootScope.$watch('mockData', function(newVal, oldVal) {
    $rootScope.mockData = newVal;
    $rootScope.mockData.travelTime=parseInt($rootScope.mockData.travelTime);
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


