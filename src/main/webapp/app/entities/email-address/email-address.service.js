(function() {
    'use strict';
    angular
        .module('contactsApp')
        .factory('EmailAddress', EmailAddress);

    EmailAddress.$inject = ['$resource'];

    function EmailAddress ($resource) {
        var resourceUrl =  'api/email-addresses/:id';

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
                url: 'api/email-addresses/contact/:contactId',
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
