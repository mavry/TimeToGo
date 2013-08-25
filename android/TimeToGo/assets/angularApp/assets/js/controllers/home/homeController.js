'use strict';

/* Controllers */

function HomeCtrl($scope, $location) {

	$scope.submit = function ()
	{
		console.log("in submit");
		 $location.path("route");
	};

}

 