(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('PhoneDetailController', PhoneDetailController);

    PhoneDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Phone', 'TypeValue', 'Contact'];

    function PhoneDetailController($scope, $rootScope, $stateParams, entity, Phone, TypeValue, Contact) {
        var vm = this;

        vm.phone = entity;

        var unsubscribe = $rootScope.$on('contactsApp:phoneUpdate', function(event, result) {
            vm.phone = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
