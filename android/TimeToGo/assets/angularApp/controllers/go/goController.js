/*global angular, moment, sprintf*/

angular.module('timeToGo.controllers'). controller('GoCtrl',  function ($scope, $rootScope,$navigate, Backend) {
  $scope.init = function() {
    $scope.data = $rootScope.data;
    $scope.data.go =  $rootScope.data.go;
    $scope.data.go.eta = moment().add($scope.data.go.drivingTime).format("hh:mm");
  };

  $scope.openWaze = function() {
    Backend.openUrl($scope.wazeURL());
  };

  $scope.wazeURL = function() {
    var urlTemplate = "waze://?ll=%s,%s&navigate=yes&z=6";
    return sprintf(urlTemplate,
    $scope.data.locations.destinationLocation.geoLocation.lat,
    $scope.data.locations.destinationLocation.geoLocation.lng);
  };

  (function() { $scope.init(); })();

});

