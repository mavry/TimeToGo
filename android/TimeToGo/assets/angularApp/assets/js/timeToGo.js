'use strict';

/* App Module */

var ROOT;


var mockedAndroidInterface = {};
var timeToGoApp = angular.module('timeToGo', ["backendServices", "simulatorServices", "localStorageServices"]);

timeToGoApp.value('prefix', 'timeToGo');
timeToGoApp.constant('cookie', { expiry:30, path: '/'});
timeToGoApp.constant('notify', { setItem: true, removeItem: false} );


timeToGoApp.config(function($routeProvider, $locationProvider ) {	
    $routeProvider.
      when('/home', {templateUrl: 'assets/templates/home/home.html', controller: 'HomeCtrl'}).
      when('/config', {templateUrl: 'assets/templates/config/config.html', controller: 'ConfigCtrl'}).
      when('/route',  {templateUrl: 'assets/templates/route/route.html', controller: 'RouteCtrl'}).
      otherwise({redirectTo: '/home'});
  }
).run(function ($rootScope, localStorageService) {

  ROOT = $rootScope;

  ROOT.fromAndroiad = {
    onCreate : function() {
        console.log("in onCreate");
        $rootScope.waitingForLocation = false;
    },
    onStart : function() {},
    onResume: function() {},
    onPause: function() {},
    updateUI: function(travelTime, routeName, updateTime) {},
    onTimeToGo: function(travelTime, routeName, updateTime) {},
    onDrivingTime: function() {},
    onCurrentLocation: function(lat, long) {
      console.log("in onCurrentLocation");
      $rootScope.waitingForLocation = false;
    },
  };
  $rootScope.history = localStorageService.get('localStorageKey') ||  {list:[]}
  localStorageService.add('localStorageKey', $rootScope.history);
});

