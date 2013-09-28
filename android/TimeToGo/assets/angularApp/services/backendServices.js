/*global angular, androidInterface */

angular.module('timeToGo.services.Backend', []).
service('Backend', function() {
  console.log("in backEnd Service. need to activate androiad");
  return {
    onGo: function (locations){
      console.log("Backend.onGo for locations "+JSON.stringify(locations));
      androidInterface.onGo(
      locations.startLocation.geoLocation.lat,
      locations.startLocation.geoLocation.lng,
      locations.destinationLocation.geoLocation.lat,
      locations.destinationLocation.geoLocation.lng);
    },
onNotify: function(startLocation, destinationLocation, maxDrivingTime) {
  console.log("Backend.onNotify startLocation: "+JSON.stringify(startLocation)+", destinationLocation: "+JSON.stringify(destinationLocation)+", maxDrivingTime: "+maxDrivingTime);
  androidInterface.onNotify(JSON.stringify(startLocation), JSON.stringify(destinationLocation), maxDrivingTime);
},
getCurrentLocation: function() {
  console.log("Backend.getCurrentLocation");
  androidInterface.getCurrentLocation();
},
openUrl: function(url) {
  console.log("Backend.openUrl url: "+url);
  androidInterface.openUrl(url);
},
  getLiveInfo: function() {
    return JSON.parse(androidInterface.getLiveInfo());
  },
  reset: function() {
    console.log("Backend.reset");
    androidInterface.reset();
  }
};
});