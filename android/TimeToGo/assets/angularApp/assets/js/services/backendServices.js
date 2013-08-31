/* Services */


// setTimeout(function () { ROOT.fromAndroiad.onCreate(); }, 3000);


angular.module('backendServices', []).
    factory('Backend', function(){

     var mockedAndroidInterface = {
      onGo: function (fromLat, fromLng, toLat, toLng) {
        Application.onDrivingTime(50);
        Application.onUpdate("50", "6 דרום", "1 min gao");
      },

      getLocation: function() {
        return '{"lat":"32.79288", "lng":"35.522935"}';
      },

      onNotify: function (maxDrivingTime) {
      //here the android will invoke onUpdate(...) each min and at the ned it will invoke onTimeToGo()
    //    setTimeout(function () { Application.onUpdate("45 min", "6 דרום", "now"); }, 3000);
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
    		onGo: function (fromLat,fromLng, toLat, toLng){ 
    			console.log("Backend.onGo"); 
    			androidInterface.onGo(fromLat,fromLng, toLat, toLng);
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