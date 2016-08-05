(function() {
    'use strict';

    angular
        .module('contactsApp')
        .controller('TypeValueDetailController', TypeValueDetailController);

    TypeValueDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TypeValue'];

    function TypeValueDetailController($scope, $rootScope, $stateParams, entity, TypeValue) {
        var vm = this;

        vm.typeValue = entity;

        var unsubscribe = $rootScope.$on('contactsApp:typeValueUpdate', function(event, result) {
            vm.typeValue = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
