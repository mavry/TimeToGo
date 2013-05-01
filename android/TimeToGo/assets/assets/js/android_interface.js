$(function () {
  var androidInterface = androidInterface || null;
  var drivingTimeVal = function () {
    return Number($("#maxDrivingTime").val()) || 0;
  };
  TouchClick("#notify", function () {
    if (androidInterface !== null) {
      androidInterface.onNotify(drivingTimeVal());
    }
    $('#myCollapse').collapse('hide');
  });

  TouchClick("#go", function () {
    if (androidInterface !== null) {
      androidInterface.onGo($("#fromAddress").val(), $("#toAddress").val());
    }
    $('#myCollapse').collapse('show');
  });

  TouchClick("#minus", function () {
    $("#maxDrivingTime").val(drivingTimeVal() - 1);
  });

  TouchClick("#plus", function () {
    $("#maxDrivingTime").val(drivingTimeVal() + 1);
  });

  function onDrivingTime(drivingTime, route, updatedAt) {
    $("#maxDrivingTime").val(drivingTime);
    $("#myCollapse").collapse('show');
  }

  function onUpdate(drivingTime, route, updatedAt) {
    $(".time-to-destination").show();
    $(".drivingTime").text(drivingTime);
    $(".route").text(route);
    $(".updatedAt").text(updatedAt);
  }

  function onTimeToGo(drivingTime, route, updatedAt) {
    onUpdate(drivingTime, route, updatedAt);
    $("#time2go").show();
  }
});

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

