'use strict';

/* App Module */

var ROOT;


var mockedAndroidInterface = {};

angular.module('timeToGo', ["backendServices", "simulatorServices"], 
  function($routeProvider, $locationProvider ) {	
    $routeProvider.
      when('/home', {templateUrl: 'assets/templates/home/home.html', controller: 'HomeCtrl'}).
      when('/config', {templateUrl: 'assets/templates/config/config.html', controller: 'ConfigCtrl'}).
      when('/route',  {templateUrl: 'assets/templates/route/route.html', controller: 'RouteCtrl'}).
      otherwise({redirectTo: '/home'});
  }
).run(function ($rootScope) {

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
  }

});

