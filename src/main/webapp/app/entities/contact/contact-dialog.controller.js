(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('ContactDialogController', ContactDialogController);

    ContactDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Contact', 'Phone', 'Address', 'EmailAddress', 'User', '$filter', 'users'];

    function ContactDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Contact, Phone, Address, EmailAddress, User, $filter, users) {
        var vm = this;

        vm.contact = entity;
        vm.selectedUser = null;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.phones = Phone.query();
        vm.addresses = Address.query();
        vm.emailaddresses = EmailAddress.query();
        // vm.users = User.query();
        vm.users = users;
        vm.userSelectionChange = userSelectionChange;

        setSelectedUser();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.contact.id !== null) {
                Contact.update(vm.contact, onSaveSuccess, onSaveError);
            } else {
                Contact.save(vm.contact, onSaveSuccess, onSaveError);
            }
        }

        function setSelectedUser() {
            if (vm.contact.userId) {
                vm.selectedUser = $filter('filter')(vm.users, {id: vm.contact.userId}, true)[0];
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('contactsApp:contactUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.birthdate = false;

        vm.setPhoto = function ($file, contact) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        contact.photo = base64Data;
                        contact.photoContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function userSelectionChange() {
            if (vm.selectedUser) {
                vm.contact.userId = vm.selectedUser.id;
            } else {
                vm.contact.userId = null;
            }
        }
    }
})();
