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
	      requested: false,
	      lastUpdated: null,
	      accuracy: -1,
	    }
	  };
	  $rootScope.data = data;	
	};




	 // $scope.$watch("currentLocation", function(newVal, oldVal){
  //  	  if (JSON.stringify(newVal)==JSON.stringify(oldVal)) return;
  //      console.log("@@ location changed " + JSON.stringify(oldVal)+" --> "+JSON.stringify(newVal));
  //      $scope.onCurrentLocation(newVal);
  //    }, true);

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
	  data.currentLocation.hasLocation = false;
	  $scope.data.currentLocation.address = "";
	  $scope.pollForLocation();
	}

	$scope.onCurrentLocation = function(geoLocation) {
	  console.log("@@ in onCurrentLocation()");
	  $rootScope.currentLocation = geoLocation; 
	  $scope.data.currentLocation.lastUpdated = moment();
      $scope.data.currentLocation.location=geoLocation;
      $scope.data.currentLocation.hasLocation = true;
      AddressForGeoLocationService.getAddressForGeoLocation($scope.data.currentLocation.location, function(address) {
      	$scope.data.currentLocation.address = address;
      });
	};
	

	$scope.onDrivingTime = function(drivingTime, routeName) {
	  console.log(sprintf("@@ in homeController onDrivingTime drivingTime=%s drivingTime=%s", drivingTime, routeName));
	  $rootScope.data.route = {
	    drivingTime: drivingTime,
	  	roadName: routeName,
	  	lastUpdated: moment()
	  }
	  $rootScope.safeApply(function(){ 
	  	$location.path("/notify/");        
      });
	};

	$scope.submit = function ()
	{
		console.log("on submit");
		HistoryService.add(data.locations.startLocation.address);
		HistoryService.add(data.locations.destinationLocation.address);
		GeoLocationForAddressService.getGeoLocationForAddress(data.locations.startLocation.address, function (geoLocation) {
			data.locations.startLocation.geoLocation = geoLocation;
			console.log("got statLocation "+JSON.stringify(geoLocation));
			GeoLocationForAddressService.getGeoLocationForAddress(data.locations.destinationLocation.address, function (geoLocation) {
				console.log("got destinationLocation "+JSON.stringify(geoLocation));
				data.locations.destinationLocation.geoLocation = geoLocation;
				Backend.onGo(data.locations);
			});			
		});
	};
 });


 