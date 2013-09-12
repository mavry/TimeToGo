angular.module('timeToGo.services.GeoLocationForAddressService', []).
//timeToGoApp.
service('GeoLocationForAddressService', function($http){
  return {
    getGeoLocationForAddress: function (address, callback) {
      var url ="http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=iw&address="+address;
      $http.get(url).success( function(data) {
        // data.results.isEmpty ?
        var geoLocation = data.results[0].geometry.location;
        callback(geoLocation);
      });
    }
  }
});