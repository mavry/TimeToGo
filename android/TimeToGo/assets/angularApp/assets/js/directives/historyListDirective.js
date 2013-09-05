/*global angular */


angular.module('timeToGo.directives').directive('historyList', function () {
    return {
      restrict: 'E',
      templateUrl: "assets/templates/home/historyList.html",

      controller: function ($scope, $element, $attrs) {
		$scope.updateFromByHistory = function(location) {
			$scope.startLocation.input=location;
		}
      }
    };
  }
);
