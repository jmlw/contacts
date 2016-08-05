(function() {
    'use strict';
    angular
        .module('contactsApp')
        .factory('Phone', Phone);

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
            'getByContact': {
                method: 'GET',
                url: 'api/phones/contact/:contactId',
                isArray: true,
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
