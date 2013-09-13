'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('HomeCtrl',  function ($scope, $rootScope, $location, Backend, HistoryService, GeoLocationForAddressService) {	

	var data;

	$scope.init = function() {
	  $scope.gPlace="";
	  data = {
	    locations: {
		  startLocation: {
	        address: ""
	      },
	      destinationLocation: {
	        address: ""
	      }
	    },
	    requestForCurrentLocatin: false
	  };
	  $rootScope.data = data;	
	  $rootScope.waitingForLocation = false;	
	};

	$scope.$watch("currentLocation", function(val1, val2){
  	  if (JSON.stringify(val1)==JSON.stringify(val2)) return;
      console.log("location changed " + JSON.stringify(val1)+" --> "+JSON.stringify(val2));
    });

	(function() { $scope.init(); })();

	$scope.startLocationFocus = function(){
	  $scope.startLocationTyping = true;
	  $rootScope.doBack = function() {  $scope.startLocationTyping = false; }
	}

	$scope.startLocationBlur = function(){
      $scope.startLocationTyping = false;
      $rootScope.doBack = null;
	}

	$scope.destinationLocationFocus = function(){
	  $scope.destinationLocationTyping = true;
	  $rootScope.doBack = function() {  $scope.destinationLocationTyping = false; }
	}

	$scope.destinationLocationBlur = function(){
      $scope.destinationLocationTyping = false;
      $rootScope.doBack = null;
	}


	$scope.obtainCurrentLocation = function() {
	  $scope.data.requestForCurrentLocatin = true;
//	  $rootScope.waitingForLocation = true;
	}

	$scope.onCurrentLocation = function(x, y) {
	  console.log("in onCurrentLocation()");
      $rootScope.waitingForLocation = false;
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


 