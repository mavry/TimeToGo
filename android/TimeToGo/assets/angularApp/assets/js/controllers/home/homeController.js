'use strict';

/* Controllers */

function HomeCtrl($scope, $rootScope, $location, Backend, localStorageService) {

    $scope.gPlace="";;

	$scope.showStartLocation = false;

	$scope.onStartLocationClick = function(){
	  $scope.showStartLocation = true;
	}

	$scope.obtainCurrentLocation = function() {
//	  $rootScope.waitingForLocation = true;
	}

	$scope.startLocation={
		input:""
	};

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

 