'use strict';

/* Controllers */

function HomeCtrl($scope, $rootScope, $location, Backend, localStorageService) {

    $scope.gPlace="";;

//	$scope.showStartLocation = false;

	$scope.startLocation={
		input:""
	};

	$scope.onStartLocationClick = function(){
	  $scope.startLocationTyping = true;
	}
	$scope.onDestinationLocationClick = function() {
		$scope.destinationLocationTyping = true;
	}

	$scope.updateFromByHistory = function(location) {
		$scope.startLocation.input=location;
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
}

 