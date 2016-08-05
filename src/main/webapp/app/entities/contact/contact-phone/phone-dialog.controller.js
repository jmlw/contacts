(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('ContactPhoneDialogController', PhoneDialogController);

    PhoneDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Phone', 'TypeValue', 'Contact'];

    function PhoneDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Phone, TypeValue, Contact) {
        var vm = this;

        vm.phone = entity;
        vm.clear = clear;
        vm.save = save;
        vm.typevalues = TypeValue.query();
        vm.contacts = Contact.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.phone.id !== null) {
                Phone.update(vm.phone, onSaveSuccess, onSaveError);
            } else {
                Phone.save(vm.phone, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('contactsApp:phoneUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
