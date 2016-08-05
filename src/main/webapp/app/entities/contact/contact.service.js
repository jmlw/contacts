(function() {
    'use strict';
    angular
        .module('contactsApp')
        .factory('Contact', Contact);

    Contact.$inject = ['$resource', 'DateUtils'];

    function Contact ($resource, DateUtils) {
        var resourceUrl =  'api/contacts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.birthdate = DateUtils.convertLocalDateFromServer(data.birthdate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.birthdate = DateUtils.convertLocalDateToServer(data.birthdate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.birthdate = DateUtils.convertLocalDateToServer(data.birthdate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
