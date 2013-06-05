var mockedAndroiadInterface = {
  onGo: function (fromAddress, toAddress) {
    onDrivingTime(50);
    onUpdate("50 min", "6 דרום", "1 min gao");
  },
  onLocation: function() {
    onCurrentLocation(32.79288,35.522935);
  },
  onNotify: function (maxDrivingTime) {
  //here the android will invoke onUpdate(...) each min and at the ned it will invoke onTimeToGo()
    setTimeout(function () { onUpdate("45 min", "6 דרום", "now"); }, 3000);
    setTimeout(function () { onTimeToGo("40 min", "6 דרום", "now"); }, 6000);
  }
};

var androidInterface = androidInterface || mockedAndroiadInterface;

androidInterface.onLocation();

TouchClick("#gps", function () {
  androidInterface.onLocation();
});


var drivingTimeVal = function () {
  return Number($("#maxDrivingTime").val()) || 0;
};

TouchClick("#notify", function () {
  $("#notificationArea").hide();
  $('#myCollapse .progress').show();
  androidInterface.onNotify(drivingTimeVal());
});

TouchClick("#go", function () {
  androidInterface.onGo($("#fromAddress").val(), $("#toAddress").val());
  $('#go').addClass('disabled');
});

TouchClick("#minus", function () {
  $("#maxDrivingTime").val(drivingTimeVal() - 1);
});

TouchClick("#plus", function () {
  $("#maxDrivingTime").val(drivingTimeVal() + 1);
});

function onCurrentLocation(lat, lng) {
	if (typeof(lat) == 'undefined') {
		$("#fromAddress").val("***");
	} else {
	    var url ="http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=iw&latlng="+lat+","+lng;
		$.getJSON(url, function(data) {
			var address = data.results[0].formatted_address;
			$("#fromAddress").val(address);

		});
	}
}

function onDrivingTime(drivingTime) {
  $("#maxDrivingTime").val(drivingTime);
  $("#myCollapse").collapse('show');
  $("#notificationArea").show();
  $("#time2go").hide();
}

function onUpdate(drivingTime, route, updatedAt) {
  $(".drivingTime").text(drivingTime);
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
}

function onTimeToGo(drivingTime, route, updatedAt) {
  $('#myCollapse .progress').hide();
  onUpdate(drivingTime, route, updatedAt);
  $("#time2go").show();
  $('#go').removeClass('disabled');
}

function TouchClick(sel, fnc) {
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
}

