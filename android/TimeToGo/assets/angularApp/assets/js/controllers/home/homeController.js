'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('HomeCtrl',  function ($scope, $rootScope, $location, Backend, localStorageService) {	

    $scope.gPlace="";;

//	$scope.showStartLocation = false;

	$scope.startLocation={
		input:""
	};

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
		console.log("on submit")
		$rootScope.history =  localStorageService.get('localStorageKey');
		$rootScope.history.list.push({name:$scope.startLocation.input})
		localStorageService.add('localStorageKey', $rootScope.history);
	};
 });


 