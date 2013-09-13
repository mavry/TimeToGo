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
	    }
	  };
	  $rootScope.data = data;	
	  $rootScope.waitingForLocation = false;	
	};

	(function() { $scope.init(); })();

	$scope.onStartLocationClick = function(){
	  $scope.startLocationTyping = true;
	  $scope.destinationLocationTyping = false;
	  $rootScope.doBack = function() {  $scope.startLocationTyping = false; }
	}

	$scope.onDestinationLocationClick = function(){
	  $scope.destinationLocationTyping = true;
	  $scope.startLocationTyping = false;
	  $rootScope.doBack = function() {  $scope.destinationLocationTyping = false; }
	}	


	$scope.obtainCurrentLocation = function() {
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


 