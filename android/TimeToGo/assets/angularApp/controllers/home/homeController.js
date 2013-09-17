'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('HomeCtrl',  function ($scope, $rootScope, $location, Backend, HistoryService, GeoLocationForAddressService, AddressForGeoLocationService, $timeout) {	

	var data;

	$scope.pollForLocation = function() {
	  console.log("poll for location");
	  Backend.getLocation();
	  // if (typeof locAsStrng === "undefined") {
	  //   console.log("*** got location: undefined");
	  // }
	  // else {
	  // 	console.log("got location: "+locAsStrng)
	  // 	var currentLocation = $.parseJSON(locAsStrng);
	  // 	$scope.onCurrentLocation(currentLocation);
	  // }
	};



	$scope.init = function() {
	  $scope.gPlace="";
	  data = {
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
	      hasLocation: false,
	      lastUpdated: null,
	      accuracy: -1,
	    }
	  };
	  $rootScope.data = data;	
	};




	 $scope.$watch("currentLocation", function(newVal, oldVal){
   	  if (JSON.stringify(newVal)==JSON.stringify(oldVal)) return;
       console.log("@@ location changed " + JSON.stringify(oldVal)+" --> "+JSON.stringify(newVal));
       $scope.onCurrentLocation(newVal);
     }, true);

	(function() { $scope.init(); })();

	$scope.startLocationFocus = function(){
	  $scope.inStartLocationField = true;
	  $rootScope.doBack = function() {  $scope.inStartLocationField = false }
	}

	$scope.startLocationBlur = function(){
      // $scope.inStartLocationField = false;
      // $rootScope.doBack = null;
	}

	$scope.destinationLocationFocus = function(){
	  $scope.inDestinationLocationField = true;
	  $rootScope.doBack = function() {  $scope.inDestinationLocationField = false; }
	}

	$scope.destinationLocationBlur = function(){
      // $scope.inDestinationLocationField = false;
      // $rootScope.doBack = null;
	}


	$scope.useCurrentLocation = function() {
	  console.log("in useCurrentLocation");
	  $scope.pollForLocation();
	}

	$scope.onCurrentLocation = function(geoLocation) {
	  console.log("@@ in onCurrentLocation()");
	  $scope.data.currentLocation.lastUpdated = moment();
      $scope.data.currentLocation.location=geoLocation;
      $scope.data.currentLocation.hasLocation = true;
      AddressForGeoLocationService.getAddressForGeoLocation($scope.data.currentLocation.location, function(address) {
      	$scope.data.currentLocation.address = address;
      });
	};
	

	$scope.submit = function ()
	{
		console.log("on submit");
		GeoLocationForAddressService.getGeoLocationForAddress(data.locations.startLocation.address, function (geoLocation) {
			data.locations.startLocation.geoLocation = geoLocation;
			console.log(JSON.stringify(geoLocation));
			GeoLocationForAddressService.getGeoLocationForAddress(data.locations.destinationLocation.address, function (geoLocation) {
				data.locations.destinationLocation.geoLocation = geoLocation;
				HistoryService.add(data.locations.startLocation.address);
				HistoryService.add(data.locations.destinationLocation.address);

				  Backend.onGo(data.locations, function(time, name, lastUpdated) {
				  	$rootScope.data.route = {
				  		drivingTime: time,
				  		roadName: name,
				  		lastUpdated: lastUpdated
				  	}
					$location.path("/notify/");
				  });
			});			
		});
	};
 });


 