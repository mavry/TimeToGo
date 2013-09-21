/* Services */

angular.module('timeToGo.services.mock.Backend', []).service('Backend', function($rootScope, $timeout, $location, localStorageService){
  console.log("mocked backEnd Service.No android is needed");
  return {
    onGo: function (locations) {
      // $timeout(function() {
      window.Application.onDrivingTime($rootScope.mockData.drivingTime, $rootScope.mockData.routeName, "1 min gao");
        // }, 3000);
    },

    getLocation: function() {
       //return JSON.stringify($rootScope.mockData.currentLocation);
       window.Application.onCurrentLocation($rootScope.mockData.currentLocation);
    },


      onNotify: function(startLocation, destinationLocation, maxDrivingTime) {
      // $timeout(function() {
      console.log("mock.Backend.onNotify startLocation: "+JSON.stringify(startLocation)+", destinationLocation: "+JSON.stringify(destinationLocation)+", maxDrivingTime: "+maxDrivingTime);

        window.Application.onTimeToGo($rootScope.mockData.drivingTime, $rootScope.mockData.routeName, "2 min ago");
      // }, 3000);

    },
    onReset: function () {
    }, 
    openUrl: function(url) {
      console.log("mock.Backend.openUrl url: "+url);
    }

  };  
});