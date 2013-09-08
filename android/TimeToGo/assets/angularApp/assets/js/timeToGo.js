'use strict';

/* App Module */

var ROOT;


var mockedAndroidInterface = {};
angular.module('timeToGo.controllers',[]);
angular.module('timeToGo.directives',[]);
angular.module('timeToGo.services',[]);

var timeToGoApp = angular.module('timeToGo', ['timeToGo.controllers', 'timeToGo.directives', 'timeToGo.services']);
timeToGoApp.value('prefix', 'timeToGo');
timeToGoApp.constant('cookie', { expiry:30, path: '/'});
timeToGoApp.constant('notify', { setItem: true, removeItem: false} );


timeToGoApp.config(function($routeProvider ) {	
    $routeProvider.
      when('/home', {templateUrl: 'assets/templates/home/home.html', controller: 'HomeCtrl'}).
      when('/config', {templateUrl: 'assets/templates/config/config.html', controller: 'ConfigCtrl'}).
      when('/route',  {templateUrl: 'assets/templates/route/route.html', controller: 'RouteCtrl'}).
      when('/timeToGo',  {templateUrl: 'assets/templates/go/go.html', controller: 'GoCtrl'}).
      otherwise({redirectTo: '/home'});
  }
).run(function ($rootScope, HistoryService, $location) {

  ROOT = $rootScope;
  HistoryService.init();
  
  ROOT.fromAndroiad = {
    onCreate : function() {
        console.log("in onCreate");
        $rootScope.waitingForLocation = false;
    },
    onStart : function() {},
    onResume: function() {},
    onPause: function() {},
    updateUI: function(travelTime, routeName, updateTime) {},
    onTimeToGo: function(travelTime, routeName, updateTime) {
      $rootScope.go = {
        travelTime: travelTime,
        routeName: routeName,
        arivalTime: moment().add(travelTime, "minutes").format("hh:mm")
      }
      $location.path("/timeToGo");
    }, 
    onDrivingTime: function() {},
    onCurrentLocation: function(lat, long) {
      console.log("in onCurrentLocation");
      $rootScope.waitingForLocation = false;
    },
  };

});

