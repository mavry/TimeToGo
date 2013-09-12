/* Services */

angular.module('timeToGo.services.Backend', []).
service('Backend', function() {
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