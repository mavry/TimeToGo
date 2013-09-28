/*global angular, sprintf */
angular.module('timeToGo.services.AddressForGeoLocationService', []).
service('AddressForGeoLocationService', function($http){
  return {
    getAddressForGeoLocation: function (geoLocation, callback) {
      var urlPattern ="http://maps.googleapis.com/maps/api/geocode/json?language=iw&sensor=true&latlng=%(lat)s,%(lng)s";
      var url = sprintf(urlPattern, geoLocation);
      $http.get(url).success( function(data) {
        // data.results.isEmpty ?
        /* jshint camelcase: false */
        var address = data.results[0].formatted_address;
        callback(address);
      });
    }
  };
});