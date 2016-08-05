(function() {
    'use strict';
    angular
        .module('contactsApp')
        .factory('ContactPhone', Phone);

    Phone.$inject = ['$resource'];

    function Phone ($resource) {
        var resourceUrl =  'api/phones/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
