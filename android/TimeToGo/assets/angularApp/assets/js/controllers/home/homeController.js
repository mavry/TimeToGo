'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('HomeCtrl',  function ($scope, $rootScope, $location, Backend, HistoryService, GeoLocationForAddressService) {	

    $scope.gPlace="";;

	$rootScope.data = $rootScope.data || {};
	$rootScope.data.startLocation = $rootScope.data.startLocation || {};
	$rootScope.data.startLocation.address = $rootScope.data.startLocation.address || "";

	$rootScope.data = $rootScope.data || {};
	$rootScope.data.destinationLocation = $rootScope.data.destinationLocation || {};
	$rootScope.data.destinationLocation.address = $rootScope.data.destinationLocation.address || "";
	

//	$scope.showStartLocation = false;

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
	
//	setTimeout(function () { $scope.fromAndroiad.onCurrentLocation("123","456"); }, 6000);


    $rootScope.waitingForLocation = false;

	$scope.submit = function ()
	{
		console.log("on submit");
		GeoLocationForAddressService.getGeoLocationForAddress($rootScope.data.startLocation.address, function (geoLocation) {
			$rootScope.data.startLocation.geoLocation = geoLocation;
			console.log(JSON.stringify(geoLocation));
			GeoLocationForAddressService.getGeoLocationForAddress($rootScope.data.destinationLocation.address, function (geoLocation) {
				$rootScope.data.destinationLocation.geoLocation = geoLocation;
				HistoryService.add($rootScope.data.startLocation.address);
				HistoryService.add($rootScope.data.destinationLocation.address);
				$location.path("/route");
			});			
		});
	};
 });


 