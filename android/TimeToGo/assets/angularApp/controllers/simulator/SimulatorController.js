/*global angular*/

angular.module('timeToGo.controllers.mock'). controller('SimulatorCtrl',  function ($rootScope, $scope, localStorageService, simulatorService) {

  $rootScope.mockData = localStorageService.get("mock") || {
    drivingTime: 51,
routeName: "road 77",
currentLocation: {
  lat: 32.6467854,
lng: 34.9891009
}
};

$rootScope.$watch('mockData', function(newVal) {
  $rootScope.mockData = newVal;
  $rootScope.mockData.drivingTime=parseInt($rootScope.mockData.drivingTime, 10);
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
$scope.onCurrentLocation = function() {
  simulatorService.onCurrentLocation($rootScope.mockData.currentLocation.lat, $rootScope.mockData.currentLocation.lng);
};


});


