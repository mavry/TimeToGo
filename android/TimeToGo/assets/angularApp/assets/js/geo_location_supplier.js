var GeoLocationProvider = {
    getGeoLocationForAddress: function (address, callback){
        var url ="http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=iw&address="+address;
        $.getJSON(url, function(data) {
    	    var location = data.results[0].geometry.location;
            callback(location);
        });
    }
};