(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('PhoneController', PhoneController);

    PhoneController.$inject = ['$scope', '$state', 'Phone', 'PhoneSearch'];

    function PhoneController ($scope, $state, Phone, PhoneSearch) {
        var vm = this;
        
        vm.phones = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Phone.query(function(result) {
                vm.phones = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            PhoneSearch.query({query: vm.searchQuery}, function(result) {
                vm.phones = result;
            });
        }    }
})();
