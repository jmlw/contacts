(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('TypeValueDialogController', TypeValueDialogController);

    TypeValueDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TypeValue'];

    function TypeValueDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TypeValue) {
        var vm = this;

        vm.typeValue = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.typeValue.id !== null) {
                TypeValue.update(vm.typeValue, onSaveSuccess, onSaveError);
            } else {
                TypeValue.save(vm.typeValue, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('contactsApp:typeValueUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
