(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('AddressDetailController', AddressDetailController);

    AddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Address', 'TypeValue', 'Contact'];

    function AddressDetailController($scope, $rootScope, $stateParams, entity, Address, TypeValue, Contact) {
        var vm = this;

        vm.address = entity;

        var unsubscribe = $rootScope.$on('contactsApp:addressUpdate', function(event, result) {
            vm.address = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
