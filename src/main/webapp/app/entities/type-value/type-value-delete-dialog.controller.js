(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('TypeValueDeleteController',TypeValueDeleteController);

    TypeValueDeleteController.$inject = ['$uibModalInstance', 'entity', 'TypeValue'];

    function TypeValueDeleteController($uibModalInstance, entity, TypeValue) {
        var vm = this;

        vm.typeValue = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TypeValue.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
