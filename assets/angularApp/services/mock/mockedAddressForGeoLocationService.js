/*global angular*/
angular.module('timeToGo.services.mock.AddressForGeoLocationService', []).
service('AddressForGeoLocationService', function(){
  return {
    getAddressForGeoLocation: function (geoLocation, callback) {
        callback("תל עדשים");
    }
  };
});