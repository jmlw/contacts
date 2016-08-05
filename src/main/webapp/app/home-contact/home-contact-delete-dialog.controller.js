(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('HomeContactDeleteController',HomeContactDeleteController);

    HomeContactDeleteController.$inject = ['$uibModalInstance', 'entity', 'Contact'];

    function HomeContactDeleteController($uibModalInstance, entity, Contact) {
        var vm = this;

        vm.contact = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Contact.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
