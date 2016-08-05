(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('ContactDetailController', ContactDetailController);

    ContactDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Contact', 'Phone', 'Address', 'EmailAddress'];

    function ContactDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Contact, Phone, Address, EmailAddress) {
        var vm = this;

        vm.contact = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('contactsApp:contactUpdate', function(event, result) {
            vm.contact = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
