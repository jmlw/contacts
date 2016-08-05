(function() {
    'use strict';

    angular
        .module('contactsApp')
        .factory('PhoneSearch', PhoneSearch);

    PhoneSearch.$inject = ['$resource'];

    function PhoneSearch($resource) {
        var resourceUrl =  'api/_search/phones/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
