(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('EmailAddressDetailController', EmailAddressDetailController);

    EmailAddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'EmailAddress', 'TypeValue', 'Contact'];

    function EmailAddressDetailController($scope, $rootScope, $stateParams, entity, EmailAddress, TypeValue, Contact) {
        var vm = this;

        vm.emailAddress = entity;

        var unsubscribe = $rootScope.$on('contactsApp:emailAddressUpdate', function(event, result) {
            vm.emailAddress = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
