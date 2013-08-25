'use strict';

/* App Module */

// angular.module('timeToGo', []).
//   config(['$routeProvider', function($routeProvider) {
angular.module('timeToGo', [], function($routeProvider, $locationProvider) {	
  $routeProvider.
      when('/home', 
      	{templateUrl: 'assets/templates/home/home.html',   controller: 'HomeCtrl'}
      ).when('/config', 
      	{templateUrl: 'assets/templates/config/config.html',   controller: 'ConfigCtrl'}
      ).when('/route', 
      	{templateUrl: 'assets/templates/route/route.html',   controller: 'RouteCtrl'}
      ).otherwise(
      	{redirectTo: '/home'}
      );
});

//}]);
