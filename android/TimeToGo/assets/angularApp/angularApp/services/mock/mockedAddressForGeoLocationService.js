angular.module('timeToGo.services.mock.AddressForGeoLocationService', []).
service('AddressForGeoLocationService', function($http){
  return {
    getAddressForGeoLocation: function (geoLocation, callback) {
        callback("תל עדשים");
    }
  };
});