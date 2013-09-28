/*global angular */


angular.module('timeToGo.directives').directive('historyList', function () {
  return {
    restrict: 'E',
scope: true,
templateUrl: "templates/home/historyList.html",

controller: function($scope, $element, $attrs) {
  $scope.model = $attrs.ngModel;
  $scope.show = $attrs.ngShow;
  $scope.update = function(location) {
    $scope.$eval($scope.model+"='"+location+"'");
    $scope.$eval($scope.show+"='"+false+"'");
  };
}
};
});
