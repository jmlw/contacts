(function() {
    'use strict';
    angular
        .module('contactsApp')
        .factory('TypeValue', TypeValue);

    TypeValue.$inject = ['$resource'];

    function TypeValue ($resource) {
        var resourceUrl =  'api/type-values/:id';

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
            'getByEntityType': {
                url: 'api/type-values/type/:entity_type',
                method: 'GET',
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
