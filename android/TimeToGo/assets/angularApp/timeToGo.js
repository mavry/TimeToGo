'use strict';

/* App Module */

var mockedAndroidInterface = {};
angular.module('timeToGo.controllers', []);
angular.module('timeToGo.directives', []);
angular.module('timeToGo.controllers.mock', []);

var fromAndroiad = {
  onCurrentLocation: function(lat, lng) {
    var scope = angular.element($(".container")[0]).scope();
    scope.safeApply(function(){ 
      scope.currentLocation = {"lat":lat, "lng":lng}; 
    });
  }
};

var moreServices = typeof androidInterface !== 'undefined' ? ['timeToGo.services.Backend','timeToGo.services.GeoLocationForAddressService'] : ['timeToGo.services.mock.Backend', 'timeToGo.services.mock.GeoLocationForAddressService'];
var angularCoreServices = ['ngRoute'];

var timeToGoApp = angular.module('timeToGo', 
  ['timeToGo.controllers', 'timeToGo.directives', 'timeToGo.controllers.mock',
  'timeToGo.services.HistoryService', 'timeToGo.services.localStorageService',
  ].concat(moreServices).concat(angularCoreServices) );

timeToGoApp.value('prefix', 'timeToGo');
timeToGoApp.constant('cookie', { expiry:30, path: '/'});
timeToGoApp.constant('notify', { setItem: true, removeItem: false} );

timeToGoApp.config(function($routeProvider ) {	
    $routeProvider.
      when('/home', {templateUrl: 'templates/home/home.html', controller: 'HomeCtrl'}).
      when('/config', {templateUrl: 'templates/config/config.html', controller: 'ConfigCtrl'}).
      when('/notify/:data',  {templateUrl: 'templates/notify/notify.html', controller: 'NotifyCtrl'}).
      when('/timeToGo',  {templateUrl: 'templates/go/go.html', controller: 'GoCtrl'}).
      otherwise({redirectTo: '/home'});
  }
).run(function ($rootScope, HistoryService, $location) {

  HistoryService.init();
  

$rootScope.safeApply = function(fn) {
  var phase = this.$root.$$phase;
  if(phase == '$apply' || phase == '$digest') {
    if(fn && (typeof(fn) === 'function')) {
      fn();
    }
  } else {
    this.$apply(fn);
  }
};


 // $rootScope.fromAndroiad = fromAndroiad;


    // onCreate: function() {
    //     console.log("in onCreate");
    //     $rootScope.waitingForLocation = false;
    // },
    // onStart: function() {},
    // onResume: function() {},
    // onPause: function() {},
    // updateUI: function(travelTime, routeName, updateTime) {},
    // onTimeToGo: function(travelTime, routeName, updateTime) {
    //   $rootScope.go = {
    //     travelTime: travelTime,
    //     routeName: routeName,
    //     arivalTime: moment().add(travelTime, "minutes").format("hh:mm")
    //   }
    //   $location.path("/timeToGo");
    // }, 
    // onDrivingTime: function() {},
    // onCurrentLocation: function(lat, long) {
    //   console.log("in onCurrentLocation");
    //   $rootScope.waitingForLocation = false;
    // },
  // };

});

