(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('EmailAddressController', EmailAddressController);

    EmailAddressController.$inject = ['$scope', '$state', 'EmailAddress', 'EmailAddressSearch'];

    function EmailAddressController ($scope, $state, EmailAddress, EmailAddressSearch) {
        var vm = this;
        
        vm.emailAddresses = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            EmailAddress.query(function(result) {
                vm.emailAddresses = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            EmailAddressSearch.query({query: vm.searchQuery}, function(result) {
                vm.emailAddresses = result;
            });
        }    }
})();
