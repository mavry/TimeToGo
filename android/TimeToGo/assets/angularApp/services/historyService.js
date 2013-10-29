/*global angular, moment */
angular.module('timeToGo.services.HistoryService', []).
service('HistoryService', function ($rootScope, localStorageService) {
    function sort(lst) {
        var as = lst.sort(function (a, b) {
            return moment(b.usedAt).diff(moment(a.usedAt));
        });
        return as;
    }
    return {
        init: function () {
            var lst = localStorageService.get('history') || {
                list: []
            };
            $rootScope.history = {
                list: sort(lst.list)
            };

            localStorageService.add('history', $rootScope.history);
        },
        add: function (address) {
            if (address === null) {
                return;
            }
            for (var i in $rootScope.history.list) {
                var item = $rootScope.history.list[i];
                if (item.name === address) {
                    item.usedAt = moment();
                    sort($rootScope.history.list);
                    localStorageService.add('history', $rootScope.history);
                    return this;
                }
            }
            var hisItem = {
                name: address,
                usedAt: moment(),
                createdAt: moment(),
            };

            $rootScope.history.list.push(hisItem);
            sort($rootScope.history.list);
            localStorageService.add('history', $rootScope.history);
            return this;
        }
    };
});
