/*global angular, $*/
angular.module('timeToGo.controllers'). controller('NavigationBarCtrl',  function ($scope, $rootScope, simulatorService) {
  $scope.simulator = simulatorService;

  $('.dropdown').find('form').click(function (e) {
    e.stopPropagation();
  });

  $scope.showBack = function(){
    return typeof($rootScope.doBack) === "function";
  };

});

