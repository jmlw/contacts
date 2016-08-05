(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('EmailAddressDeleteController',EmailAddressDeleteController);

    EmailAddressDeleteController.$inject = ['$uibModalInstance', 'entity', 'EmailAddress'];

    function EmailAddressDeleteController($uibModalInstance, entity, EmailAddress) {
        var vm = this;

        vm.emailAddress = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            EmailAddress.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
