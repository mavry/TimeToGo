var mockedAndroidInterface = {
  onGo: function (fromLat, fromLng, toLat, toLng) {
    Application.onDrivingTime(50);
    Application.onUpdate("50", "6 דרום", "1 min gao");
  },

  getLocation: function() {
    return '{"lat":"32.79288", "lng":"35.522935"}';
  },

  onNotify: function (maxDrivingTime) {
  //here the android will invoke onUpdate(...) each min and at the ned it will invoke onTimeToGo()
    setTimeout(function () { Application.onUpdate("45 min", "6 דרום", "now"); }, 3000);
    setTimeout(function () { Application.onTimeToGo("40 min", "6 דרום", "now"); }, 6000);
  },
  onReset: function () {
  }
};

window.Application = {
  inititialize: function () {
    var self = this;
    self.androidInterface = typeof androidInterface !== 'undefined' ? androidInterface : mockedAndroidInterface;
    if (self.androidInterface === mockedAndroidInterface) {
      self.onCreate();
      setTimeout(function () { self.onCurrentLocation(32.79288, 35.522935, "mock"); }, 3000);
    }

    var TouchClick = function (sel, fnc) {
      $(sel).on('touchstart click', function (event) {
        event.stopPropagation();
        event.preventDefault();
        if (event.handled !== true) {
          fnc(event);
          event.handled = true;
        } else {
          return false;
        }
      });
    };

    TouchClick("#dismiss", function () {
      self.onResetPage();
      self.androidInterface.onReset();
    });


    TouchClick("#notify", function () {
      $("#notificationArea").addClass('hide');
      $('#myCollapse .progress').removeClass('hide');
      self.androidInterface.onNotify(self.drivingTimeVal());
    });

    TouchClick("#go", function () {
      var fromAddress = $("#fromAddress").val();
      var myLocation = JSON.parse(self.androidInterface.getLocation());
      // alert("myLocation = "+JSON.stringify(myLocation));


      GeoLocationProvider.getGeoLocationForAddress($("#toAddress").val(), function (toLocation) {
        if (self.isMyLocation(fromAddress)) {
          self.androidInterface.onGo(self.myFromLocation.lat, self.myFromLocation.lng, toLocation.lat, toLocation.lng);
        }
        else
        {
          GeoLocationProvider.getGeoLocationForAddress($("#fromAddress").val(), function (fromLocation) {
            self.androidInterface.onGo(fromLocation.lat, fromLocation.lng, toLocation.lat, toLocation.lng);
          });
        }
      });
      $('#go').addClass('disabled');
    });

    TouchClick("#plus", function () {
      $("#maxDrivingTime").val(self.drivingTimeVal() + 1);
    });

    TouchClick("#minus", function () {
      $("#maxDrivingTime").val(self.drivingTimeVal() - 1);
    });

//    function getLocation () {
//      return JSON.parse(self.androidInterface.getLocation());
//    }

  },

  onResetPage: function () {
    $("#myCollapse").collapse('hide');
    $('#notificationArea').addClass('hide');
    $('#go').removeClass('disabled');
  },

  drivingTimeVal: function () {
    return Number($("#maxDrivingTime").val()) || 0;
  },

  isMyLocation: function (address) {
    return (address.indexOf("My Location") >= 0 || address.indexOf("מיקום שלי") >= 0);
  },

  onCurrentLocation: function (lat, lng, provider) {
    if (typeof(lat) == 'undefined') {
      $("#fromAddress").val("***");
    }
    else {
      $('#fromAddress').removeClass('loadinggif');
      this.myFromLocation = {lat: lat, lng: lng};
      var url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=iw&latlng=" + lat + "," + lng;
      $.getJSON(url, function (data) {
        var address = "מיקום שלי-" + provider + "-" + data.results[0].formatted_address;
        $("#fromAddress").val(address);
      });
    }
  },

  onCreate:  function () {
    $('#fromAddress').addClass('loadinggif');
  },

  onStart : function () {

  },

  onResume : function () {

  },
  onPause: function () {

  },

  onDrivingTime: function (drivingTime) {
    $("#maxDrivingTime").val(drivingTime);
    $("#myCollapse").collapse('show');
    $("#notificationArea").removeClass('hide');
    $("#time2go").addClass('hide');
  },

  onUpdate: function (drivingTime, route, updatedAt) {
    $(".drivingTime").text(drivingTime+" min");
    $(".drivingTime").css("-webkit-transition", "all 0.6s ease")
      .css("backgroundColor", "transparent")
      .css("-moz-transition", "all 0.6s ease")
      .css("-o-transition", "all 0.6s ease")
      .css("-ms-transition", "all 0.6s ease")
      .css("backgroundColor", "white").delay(200).queue(function () {
          $(this).css("backgroundColor", "transparent");
          $(this).dequeue(); //Prevents box from holding color with no fadeOut on second click.
        });
    $(".route").text(route);
    $(".updatedAt").text(updatedAt);
  },

  onTimeToGo: function (drivingTime, route, updatedAt) {
    $('#myCollapse .progress').addClass('hide');
    this.onUpdate(drivingTime, route, updatedAt);
    $("#time2go").removeClass('hide');
    $('#go').removeClass('disabled');
  }
};
window.Application.inititialize();
