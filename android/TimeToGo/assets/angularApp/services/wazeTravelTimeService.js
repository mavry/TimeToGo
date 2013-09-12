//angular.module('timeToGo', []).
timeToGoApp.
service('WazeTravelTimeService', function($http){
  return {
    getRoute: function (startLocation, destinationLocation, callback) {
      var urlPattern="http://www.waze.co.il/RoutingManager/routingRequest?to=%(destination.lng)s+%(destination.lat)s+%(bd)s&from=%(start.lng)s+%(start.lat)s+%(bd)s+%(s)s+%(st_id)s&returnJSON=true";
      var data = {
        destination: {
          lng: encodeURIComponent("x:"+destinationLocation.geoLocation.lng),
          lat: encodeURIComponent("y:"+destinationLocation.geoLocation.lat)
        }, 
        start : {
          lng: encodeURIComponent("x:"+startLocation.geoLocation.lng),
          lat: encodeURIComponent("y:"+startLocation.geoLocation.lat)          
        },
        bd: encodeURIComponent("bd:true"),
        s: encodeURIComponent("s:37001"),
        st_id: encodeURIComponent("st_id:12604"),

      }
      var url = sprintf(urlPattern, data);
      $http.get(url).success( function(data) {
        // data.results.isEmpty ?
        var seonds = 0;
        for (r in response.results) {
          seonds += response.results[r].crossTime;
        }
        var route = {
          name: data.response.routeName, 
          travelTime: Math.round(seonds/60)
        };
        console.log(JSON.sringify(route));
       // callback(route);
      });

    }
  };
});