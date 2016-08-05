(function() {
    'use strict';

    angular
        .module('contactsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('email-address', {
            parent: 'entity',
            url: '/email-address',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'contactsApp.emailAddress.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/email-address/email-addresses.html',
                    controller: 'EmailAddressController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('emailAddress');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('email-address-detail', {
            parent: 'entity',
            url: '/email-address/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'contactsApp.emailAddress.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/email-address/email-address-detail.html',
                    controller: 'EmailAddressDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('emailAddress');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'EmailAddress', function($stateParams, EmailAddress) {
                    return EmailAddress.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('email-address.new', {
            parent: 'email-address',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-address/email-address-dialog.html',
                    controller: 'EmailAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                email: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('email-address', null, { reload: true });
                }, function() {
                    $state.go('email-address');
                });
            }]
        })
        .state('email-address.edit', {
            parent: 'email-address',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-address/email-address-dialog.html',
                    controller: 'EmailAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['EmailAddress', function(EmailAddress) {
                            return EmailAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('email-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('email-address.delete', {
            parent: 'email-address',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/email-address/email-address-delete-dialog.html',
                    controller: 'EmailAddressDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['EmailAddress', function(EmailAddress) {
                            return EmailAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('email-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
