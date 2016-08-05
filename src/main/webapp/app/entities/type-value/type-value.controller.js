(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('TypeValueController', TypeValueController);

    TypeValueController.$inject = ['$scope', '$state', 'TypeValue', 'TypeValueSearch'];

    function TypeValueController ($scope, $state, TypeValue, TypeValueSearch) {
        var vm = this;
        
        vm.typeValues = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            TypeValue.query(function(result) {
                vm.typeValues = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TypeValueSearch.query({query: vm.searchQuery}, function(result) {
                vm.typeValues = result;
            });
        }    }
})();
