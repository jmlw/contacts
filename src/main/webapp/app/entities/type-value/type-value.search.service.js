(function() {
    'use strict';

    angular
        .module('contactsApp')
        .factory('TypeValueSearch', TypeValueSearch);

    TypeValueSearch.$inject = ['$resource'];

    function TypeValueSearch($resource) {
        var resourceUrl =  'api/_search/type-values/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
