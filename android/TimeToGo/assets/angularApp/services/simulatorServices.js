angular.module('timeToGo.services.localStorageService', []).
factory('simulatorService', function($rootScope){
  return {
    onCreate: function() {
      $rootScope.fromAndroaid.onCreate();
    },
    onStart: function() {
      $rootScope.fromAndroaid.onStart();
    },
    onResume: function() {
      $rootScope.fromAndroaid.onResume();
    },
    onPause: function() {
      $rootScope.fromAndroaid.onPause();
    },
    updateUI: function() {
      $rootScope.updateUI();
    },
    onTimeToGo: function() {
      $rootScope.fromAndroaid.onTimeToGo();
    },
    onDrivingTime: function() {
      $rootScope.fromAndroaid.onDrivingTime();
    },
    onCurrentLocation: function(lat, lng) {
      console.log("onCurrentLocation "+lat+" / "+lng);
      fromAndroiad.onCurrentLocation({"lat":lat, "lng":lng});
    }
  } 
});

