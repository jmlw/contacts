(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('AddressController', AddressController);

    AddressController.$inject = ['$scope', '$state', 'Address', 'AddressSearch'];

    function AddressController ($scope, $state, Address, AddressSearch) {
        var vm = this;

        vm.addresses = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Address.query(function(result) {
                vm.addresses = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AddressSearch.query({query: vm.searchQuery}, function(result) {
                vm.addresses = result;
            });
        }    }
})();
