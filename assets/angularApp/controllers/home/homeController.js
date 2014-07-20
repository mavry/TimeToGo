/*global angular, moment, sprintf*/

angular.module('timeToGo.controllers'). controller('HomeCtrl',  function ($scope, $rootScope, $navigate, $location, Backend, HistoryService, GeoLocationForAddressService, AddressForGeoLocationService) {

  var data;

  $scope.retreiveCurrentLocation = function() {
    console.log("retreiveCurrentLocation for location");
    Backend.getCurrentLocation();
  };



  $scope.init = function() {
    $scope.gPlace="";
    data = $rootScope.data ||
{
  locations: {
    startLocation: {
      address: null
    },
  destinationLocation: {
    address: null
  }
  },
    currentLocation: {
      location: null,
      requested: false,
      lastUpdated: null,
      accuracy: -1,
    }
};
$rootScope.data = data;
};




// $scope.$watch("currentLocation", function(newVal, oldVal){
//      if (JSON.stringify(newVal)==JSON.stringify(oldVal)) return;
//      console.log("@@ location changed " + JSON.stringify(oldVal)+" --> "+JSON.stringify(newVal));
//      $scope.onCurrentLocation(newVal);
//    }, true);

(function() { $scope.init(); })();

$scope.startLocationFocus = function(){
  $scope.inStartLocationField = true;
  $scope.inDestinationLocationField = false;
  $rootScope.doBack = function() {  $scope.inStartLocationField = false; };
};

$scope.destinationLocationFocus = function(){
  $scope.inDestinationLocationField = true;
  $scope.inStartLocationField = false;
  $rootScope.doBack = function() {  $scope.inDestinationLocationField = false; };
};


$scope.onCurrentLocationRequest = function() {
  $scope.data.currentLocation.requested=true;
  $scope.data.locations.startLocation.address = null;
  // $scope.inDestinationLocationField = true;
  $scope.inStartLocationField = false;
};


$scope.onCurrentLocation = function(geoLocation) {
  console.log("@@ in onCurrentLocation()");
  $rootScope.currentLocation = geoLocation;
  $scope.data.currentLocation.lastUpdated = moment();
  $scope.data.currentLocation.location=geoLocation;
  $scope.data.locations.startLocation.geoLocation=geoLocation;

  AddressForGeoLocationService.getAddressForGeoLocation($scope.data.currentLocation.location, function(address) {
    $scope.data.currentLocation.address = address;
    $scope.data.locations.startLocation.address = address;
    // duplicate code...
    console.log("@@ got address for current location "+address);
    GeoLocationForAddressService.getGeoLocationForAddress(data.locations.destinationLocation.address, function (geoLocation) {
      console.log("got destinationLocation "+JSON.stringify(geoLocation));
      data.locations.destinationLocation.geoLocation = geoLocation;
      Backend.onGo(data.locations);
    });
  });
};

$scope.isSubmitDisabled = function() {
  var enabled =
    (
      ($scope.data.locations.destinationLocation.address !== null &&  $scope.data.locations.destinationLocation.address.length > 3 ) && ( (   $scope.data.locations.startLocation.address !== null && $scope.data.locations.startLocation.address.length > 3 ) || $scope.data.currentLocation.requested === true )
    );
  console.log("$scope.data.locations.destinationLocation.address "+$scope.data.locations.destinationLocation.address);
  console.log("enabled = "+enabled);

  return !enabled;
};

$scope.onDrivingTime = function(drivingTime, routeName) {
  console.log(sprintf("@@ in homeController onDrivingTime drivingTime=%s drivingTime=%s", drivingTime, routeName));
  $rootScope.data.route = {
    drivingTime: drivingTime,
    routeName: routeName,
    updatedAt: moment()
  };

  $rootScope.safeApply(function(){
    $navigate.go('/notify/');
  });
};

$scope.submit = function ()
{
  console.log("on submit");
  HistoryService.add(data.locations.startLocation.address);
  HistoryService.add(data.locations.destinationLocation.address);
  if (data.currentLocation.requested) {
    $scope.retreiveCurrentLocation();
  } else {
    GeoLocationForAddressService.getGeoLocationForAddress(data.locations.startLocation.address, function (geoLocation) {
      data.locations.startLocation.geoLocation = geoLocation;
      console.log("got statLocation "+JSON.stringify(geoLocation));
      GeoLocationForAddressService.getGeoLocationForAddress(data.locations.destinationLocation.address, function (geoLocation) {
        console.log("got destinationLocation "+JSON.stringify(geoLocation));
        data.locations.destinationLocation.geoLocation = geoLocation;
        Backend.onGo(data.locations);
      });
    });
  }
};
});


