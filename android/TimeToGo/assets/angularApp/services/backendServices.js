/*global angular, androidInterface */

angular.module('timeToGo.services.Backend', []).
service('Backend', function() {
  console.log("in backEnd Service. need to activate androiad");
	return {
      onGo: function (locations, callback){
		console.log("Backend.onGo");
		androidInterface.onGo(locations.startLocation.geoLocation.lat,locations.startLocation.geoLocation.lng,
		locations.destinationLocation.geoLocation.lat, locations.destinationLocation.geoLocation.lng, callback);
	},
      onNotify: function(maxTravelTime) {
		console.log("Backend.onNotify");
		androidInterface.onNotify(maxTravelTime);
	},
      getLocation: function() {
		console.log("Backend.getLocation");
		androidInterface.getLocation();
      }
	};
});