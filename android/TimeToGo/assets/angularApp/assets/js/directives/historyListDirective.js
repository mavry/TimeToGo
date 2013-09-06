/*global angular */


angular.module('timeToGo.directives').directive('historyList', function () {
  return {
    restrict: 'E',
    scope: true, 
    templateUrl: "assets/templates/home/historyList.html",

    controller: function($scope, $element, $attrs) {
      $scope.x = $attrs.ngModel;
      $scope.attrs = $attrs;
      $scope.update = function(location) {
        console.log("about to chnage: "+$scope.x);
        $scope.$eval($scope.x+"='"+location+"'");
	    }
    }
  };
});
