angular.module('timeToGo.services.mock.GeoLocationForAddressService', []).
service('GeoLocationForAddressService', function($http){
  return {
    getGeoLocationForAddress: function (address, callback) {
      alert("in getGeoLocationForAddress");
      callback({lng:10, lat:20});
    }
  }
});