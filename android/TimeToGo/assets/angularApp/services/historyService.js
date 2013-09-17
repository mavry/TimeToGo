/*global angular */

angular.module('timeToGo.services.HistoryService', []).
service('HistoryService', function($rootScope, localStorageService){
  return {
    init: function() {
      $rootScope.history = localStorageService.get('history') ||  {list:[]};
      localStorageService.add('history', $rootScope.history);
    },
    add: function (address) {
      for (var item in $rootScope.history.list)
      {
        if ($rootScope.history.list[item].name===address){
          return this;
        }
      }
      $rootScope.history.list.push({name:address});
      localStorageService.add('history', $rootScope.history);
      return this;
    }
  };
});