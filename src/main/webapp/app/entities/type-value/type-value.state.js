(function() {
    'use strict';

    angular
        .module('contactsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('type-value', {
            parent: 'entity',
            url: '/type-value',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'contactsApp.typeValue.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-value/type-values.html',
                    controller: 'TypeValueController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeValue');
                    $translatePartialLoader.addPart('entityType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('type-value-detail', {
            parent: 'entity',
            url: '/type-value/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'contactsApp.typeValue.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-value/type-value-detail.html',
                    controller: 'TypeValueDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeValue');
                    $translatePartialLoader.addPart('entityType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TypeValue', function($stateParams, TypeValue) {
                    return TypeValue.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('type-value.new', {
            parent: 'type-value',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-value/type-value-dialog.html',
                    controller: 'TypeValueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                entityType: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('type-value', null, { reload: true });
                }, function() {
                    $state.go('type-value');
                });
            }]
        })
        .state('type-value.edit', {
            parent: 'type-value',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-value/type-value-dialog.html',
                    controller: 'TypeValueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TypeValue', function(TypeValue) {
                            return TypeValue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-value', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('type-value.delete', {
            parent: 'type-value',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-value/type-value-delete-dialog.html',
                    controller: 'TypeValueDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TypeValue', function(TypeValue) {
                            return TypeValue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-value', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
