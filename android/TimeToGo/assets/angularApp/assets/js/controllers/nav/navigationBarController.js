'use strict';

/* Controllers */

angular.module('timeToGo.controllers'). controller('NavigationBarCtrl',  function ($scope, $rootScope, simulator) {	
  $scope.simulator = simulator;
  $scope.showBack = function(){
  	return typeof($rootScope.doBack) == "function";
  }

});

 