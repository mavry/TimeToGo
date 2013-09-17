'use strict';

/* App Module */

var mockedAndroidInterface = {};
var ROOT;
angular.module('timeToGo.controllers', []);
angular.module('timeToGo.directives', []);
angular.module('timeToGo.controllers.mock', []);


var moreServices = (typeof androidInterface !== 'undefined' ) ? ['timeToGo.services.Backend','timeToGo.services.GeoLocationForAddressService','timeToGo.services.AddressForGeoLocationService'] : ['timeToGo.services.mock.Backend', 'timeToGo.services.mock.GeoLocationForAddressService','timeToGo.services.AddressForGeoLocationService'];
var angularCoreServices = ['ngRoute'];
var Application = {};
var timeToGoApp = angular.module('timeToGo', 
  ['LocalStorageModule', 'timeToGo.controllers', 'timeToGo.directives', 'timeToGo.controllers.mock',
    'timeToGo.services.SimulatorService',
  'timeToGo.services.HistoryService',
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

 ROOT = $rootScope;
});


window.Application = {

  inititialize: function () {
    var self = this;
    if (typeof androidInterface !== 'undefined' ) {
      self.androidInterface = androidInterface;
      console.log("we are in PROD");
    }
    else
    {
      console.log("we are in SIMULATOR");
      self.androidInterface = mockedAndroidInterface;
    }    
  },


    onCreate: function() {
        console.log("on onCreate");
    },
    onStart: function() {
      console.log("on Start");
    },
    onResume: function() {
      console.log("on onResume");
    },
    onPause: function() {
      console.log("on Pause");
    },
    updateUI: function(maxDrivingTime, routeName, lastUpdated) {
      console.log(sprintf("on updateUI with maxDrivingTime=%(maxDrivingTime)s routeName=%(routeName)s", 
        maxDrivingTime, routeName));
    },
    onTimeToGo: function(maxDrivingTime, routeName, lastUpdated) {
      console.log(sprintf("on onTimeToGo with maxDrivingTime=%(maxDrivingTime)s routeName=%(routeName)s", 
        maxDrivingTime, routeName));
    },
    onDrivingTime: function(maxDrivingTime) {
      console.log(sprintf("on onDrivingTime with maxDrivingTime = %(maxDrivingTime)s", maxDrivingTime));
    },
    onCurrentLocation: function(geoLocation) {
      console.log("@@ onCurrentLocation = "+geoLocation);
      console.log("@@ onCurrentLocation "+geoLocation.lat+" / "+geoLocation.lng);
      ROOT.safeApply(function(){ 
        ROOT.currentLocation = geoLocation; 
      });
    }
  };
window.Application.inititialize();