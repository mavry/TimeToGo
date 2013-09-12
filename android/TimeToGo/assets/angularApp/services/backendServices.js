/* Services */


// setTimeout(function () { ROOT.fromAndroiad.onCreate(); }, 3000);


//angular.module('timeToGo', []).
timeToGoApp.
service('Backend', function($rootScope, $timeout, $location, localStorageService){

  $rootScope.mock = localStorageService.get("timeToGo.mock");
  $rootScope.mock = $rootScope.mock  || {
    travelTime: 51,
    roadName: "road 7",
    currentLocation: {
      lat: 31.959261,
      lng: 34.815673
    }
  };

  localStorageService.add("timeToGo.mock", $rootScope.mock);

  var mockedAndroidInterface = {
      onGo: function (fromLat, fromLng, toLat, toLng, callback) {
        $timeout(function() {
            callback($rootScope.mock.travelTime, $rootScope.mock.roadName, "1 min gao")}
          , 3000);
      },

      getLocation: function() {
        return JSON.stringify($rootScope.mock.currentLocation);
      },

      onNotify: function (maxDrivingTime) {
        $timeout(function() {
          $rootScope.fromAndroiad.onTimeToGo($rootScope.mock.travelTime, $rootScope.mock.roadName, "2 min ago");
        }, 3000);
      //  here the android will invoke onUpdate(...) each min and at the ned it will invoke onTimeToGo()
      //  setTimeout(function () { Application.onUpdate("45 min", "6 דרום", "now"); }, 3000);
      //  setTimeout(function () { Application.onTimeToGo("40 min", "6 דרום", "now"); }, 6000);
      },
      onReset: function () {
      }
    };

    var androidInterface = typeof androidInterface !== 'undefined' ? androidInterface : mockedAndroidInterface;
    
    if (androidInterface === mockedAndroidInterface) {
      //ROOT.fromAndroiad.onCreate();
    } 

    	console.log("in backEnd Service. need to activate androiad");
    	return {
    		onGo: function (fromLat,fromLng, toLat, toLng, callback){ 
    			console.log("Backend.onGo"); 
    			androidInterface.onGo(fromLat,fromLng, toLat, toLng, callback);
    		}, 
    		onNotify: function(maxTravelTime) {
    			console.log("Backend.onNotify");
				  androidInterface.onNotify(maxTravelTime);
    		},
    		getLocation: function() {
				  console.log("Backend.getLocation");
				  return androidInterface.getLocation();
    		}
    	};
  });