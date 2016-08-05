(function() {
    'use strict';

    angular
        .module('contactsApp')
        .factory('EmailAddressSearch', EmailAddressSearch);

    EmailAddressSearch.$inject = ['$resource'];

    function EmailAddressSearch($resource) {
        var resourceUrl =  'api/_search/email-addresses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
