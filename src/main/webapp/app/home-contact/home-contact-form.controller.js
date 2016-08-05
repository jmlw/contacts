(function () {
    'use strict';

    angular
        .module('contactsApp')
        .controller('HomeContactFormController', HomeContactFormController);

    HomeContactFormController.$inject = ['$timeout', '$scope', '$stateParams', 'DataUtils', 'entity', 'Contact',
        'Phone', 'Address', 'EmailAddress', 'User', '$filter', 'users', '$state', 'TypeValue'];

    function HomeContactFormController($timeout, $scope, $stateParams, DataUtils, entity, Contact, Phone, Address,
                                       EmailAddress, User, $filter, users, $state, TypeValue) {
        var vm = this;


        vm.contact = entity;
        vm.selectedUser = null;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.phoneTypeValues = TypeValue.getByEntityType({'entity_type': 'PHONE_TYPE'});
        vm.emailTypeValues = TypeValue.getByEntityType({'entity_type': 'EMAIL_TYPE'});
        vm.addressTypeValues = TypeValue.getByEntityType({'entity_type': 'ADDRESS_TYPE'});
        vm.users = users;
        vm.userSelectionChange = userSelectionChange;
        vm.addPhone = addPhone;
        vm.addAddress = addAddress;
        vm.addEmail = addEmail;
        vm.removePhone = removePhone;
        vm.removeAddress = removeAddress;
        vm.removeEmail = removeEmail;

        vm.phoneNumbersToDelete = [];
        vm.emailAddressesToDelete = [];
        vm.addressesToDelete = [];

        setSelectedUser();

        $timeout(function () {
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear() {
            if (vm.contact.id !== null) {
                $state.go('home-contact-detail', {contactId: vm.contact.id});
            } else {
                $state.go('home');
            }
        }

        function setSelectedUser() {
            if (vm.contact.userId) {
                vm.selectedUser = $filter('filter')(vm.users, {id: vm.contact.userId}, true)[0];
            }
        }

        function saveSubRecords() {
            vm.contact.phoneNumbers.forEach(function (phone) {
                if (phone.id !== null) {
                    Phone.update(phone);
                } else {
                    Phone.save(phone);
                }
            });
            vm.contact.emailAddresses.forEach(function (email) {
                if (email.id !== null) {
                    EmailAddress.update(email);
                } else {
                    EmailAddress.save(email);
                }
            });
            vm.contact.addresses.forEach(function (address) {
                if (address.id !== null) {
                    Address.update(address);
                } else {
                    Address.save(address);
                }
            });
        }

        function deleteSubRecords() {
            vm.phoneNumbersToDelete.forEach(function (phone) {
                Phone.delete({id: phone.id});
            });
            vm.emailAddressesToDelete.forEach(function (email) {
                EmailAddress.delete({id: email.id});
            });
            vm.addressesToDelete.forEach(function (address) {
                Address.delete({id: address.id});
            });
        }

        function save() {
            vm.isSaving = true;
            if (vm.contact.id !== null) {
                deleteSubRecords();
                saveSubRecords();

                Contact.update(vm.contact, onSaveSuccess, onSaveError);
            } else {

                Contact.save(vm.contact, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('contactsApp:contactUpdate', result);
            if (vm.contact.id !== null) {
                $state.go('home-contact-detail', {contactId: vm.contact.id});
            } else {
                $state.go('home');
            }
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.birthdate = false;

        vm.setPhoto = function ($file, contact) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function (base64Data) {
                    $scope.$apply(function () {
                        contact.photo = base64Data;
                        contact.photoContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function userSelectionChange() {
            if (vm.selectedUser) {
                vm.contact.userId = vm.selectedUser.id;
            } else {
                vm.contact.userId = null;
            }
        }

        function addPhone() {
            vm.contact.phoneNumbers.push({
                id: null,
                phoneNumber: null,
                type: null,
                contactId: vm.contact.id
            });
        }

        function addAddress() {
            vm.contact.addresses.push({
                city: null,
                zip: null,
                state: null,
                stateAbbr: null,
                streetAddress: null,
                id: null,
                type: null,
                contactId: vm.contact.id
            });
        }

        function addEmail() {
            vm.contact.emailAddresses.push({
                email: null,
                id: null,
                type: null,
                contactId: vm.contact.id
            });
        }

        function removePhone(index) {
            var tempId = vm.contact.phoneNumbers[index].id;
            if (tempId) {
                vm.phoneNumbersToDelete.push(vm.contact.phoneNumbers[index]);
            }
            vm.contact.phoneNumbers.splice(index, 1);
        }

        function removeAddress(index) {
            var tempId = vm.contact.addresses[index].id;
            if (tempId) {
                vm.addressesToDelete.push(vm.contact.addresses[index]);
            }
            vm.contact.addresses.splice(index, 1);
        }

        function removeEmail(index) {
            var tempId = vm.contact.emailAddress[index].id;
            if (tempId) {
                vm.emailAddressesToDelete.push(vm.contact.emailAddress[index]);
            }
            vm.contact.emailAddress.splice(index, 1);
        }
    }
})();
