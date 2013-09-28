/*global angular, moment*/

angular.module('timeToGo.services.mock.Backend', []).service('Backend', function($rootScope, $timeout){
  console.log("mocked backEnd Service.No android is needed");


  function isItTimeToGo() {
    return $rootScope.mockData.drivingTime <= $rootScope.data.notification.request.maxDrivingTime;
  }

  function simulateNotify() {
    $timeout(function() {
      if (isItTimeToGo()) {
        window.Application.onTimeToGo($rootScope.mockData.drivingTime, $rootScope.mockData.routeName, "2 min ago");
      } else
    {
      simulateNotify();
    }
    }, 1000);
  }

  return {
    onGo: function () {
      $timeout(function() {
        window.Application.onDrivingTime($rootScope.mockData.drivingTime, $rootScope.mockData.routeName, "1 min gao");
      }, 3000);
    },
      getCurrentLocation: function() {
        $timeout(function() {
          window.Application.onCurrentLocation($rootScope.mockData.currentLocation);
        }, 3000);
      },


      onNotify: function(startLocation, destinationLocation, maxDrivingTime) {
        console.log("mock.Backend.onNotify startLocation: "+JSON.stringify(startLocation)+", destinationLocation: "+JSON.stringify(destinationLocation)+", maxDrivingTime: "+maxDrivingTime);
        simulateNotify();
      },

      onReset: function () {
      },
      openUrl: function(url) {
        console.log("mock.Backend.openUrl url: "+url);
      },
      getLiveInfo: function() {
        console.log("@@ mock.Backend.getLive Info");
        var liveInfo = {drivingTime: $rootScope.mockData.drivingTime, routeName: $rootScope.mockData.routeName, timeToGo: isItTimeToGo(), updatedAt: moment()};
        console.log("@@ liveInfo = "+liveInfo);
        return liveInfo;
      },
      reset: function() {
        console.log("mock.Backend.reset");
      }
  };
});