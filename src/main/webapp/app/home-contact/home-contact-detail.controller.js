(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('HomeContactDetailController', HomeContactDetailController);

    HomeContactDetailController.$inject = ['$state', '$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Contact', 'Phone', 'Address', 'EmailAddress'];

    function HomeContactDetailController($state, $scope, $rootScope, $stateParams, DataUtils, entity, Contact, Phone, Address, EmailAddress) {
        var vm = this;

        vm.contact = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.back = back;

        function back() {
            $state.go('home');
        }

        var unsubscribe = $rootScope.$on('contactsApp:contactUpdate', function(event, result) {
            vm.contact = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
