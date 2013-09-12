'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('GoCtrl',  function ($scope, $rootScope) {	
	moment().add(142, "minutes").format("hh:mm")

});

