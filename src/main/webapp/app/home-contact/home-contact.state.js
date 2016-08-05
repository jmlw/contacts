(function() {
    'use strict';

    angular
        .module('contactsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'app',
                url: '/',
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home-contact/home-contacts.html',
                        controller: 'HomeContactController',
                        controllerAs: 'vm'
                    }
                },
                params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'id,asc',
                        squash: true
                    },
                    search: null
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort),
                            search: $stateParams.search
                        };
                    }],
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('contact');
                        return $translate.refresh();
                    }]
                }
            })
            .state('home-contact-detail', {
                parent: 'home',
                url: 'contacts/{contactId:[0-9]+}',
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home-contact/home-contact-detail.html',
                        controller: 'HomeContactDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('contact');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Contact', function($stateParams, Contact) {
                        return Contact.get({id : $stateParams.contactId}).$promise;
                    }]
                }
                // onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                //     $uibModal.open({
                //         templateUrl: '/app/home-contact/home-contact-detail.html',
                //         controller: 'HomeContactDetailController',
                //         controllerAs: 'vm',
                //         backdrop: 'static',
                //         size: 'lg',
                //         resolve: {
                //             translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                //                 $translatePartialLoader.addPart('contact');
                //                 return $translate.refresh();
                //             }],
                //             entity: ['$stateParams', 'Contact', function($stateParams, Contact) {
                //                 return Contact.get({id : $stateParams.conatactId}).$promise;
                //             }]
                //         }
                //     }).result.then(function() {
                //         $state.go('home', null, {reload: true});
                //     }, function() {
                //         $state.go('home');
                //     });
                // }]
            })
            .state('home-contact-edit', {
                parent: 'home',
                url: 'contacts/{contactId}/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home-contact/home-contact-form.html',
                        controller: 'HomeContactFormController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('contact');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Contact', function($stateParams, Contact) {
                        return Contact.get({id: $stateParams.contactId}).$promise;
                    }],
                    users: ['User', function(User) {
                        return User.query();
                    }]
                }
            })
            .state('home-contact-new', {
                parent: 'home',
                url: 'contacts/new',
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home-contact/home-contact-form.html',
                        controller: 'HomeContactFormController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('contact');
                        return $translate.refresh();
                    }],
                    entity: function () {
                        return {
                            firstName: null,
                            lastName: null,
                            nickName: null,
                            company: null,
                            jobTitle: null,
                            birthdate: null,
                            website: null,
                            photo: null,
                            photoContentType: null,
                            notes: null,
                            id: null
                        }
                    },
                    users: ['User', function(User) {
                        return User.query();
                    }]
                }
            })
            .state('home-contact-delete', {
                parent: 'home',
                url: 'contacts/{contactId}/delete',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/home-contact/home-contact-delete-dialog.html',
                        controller: 'HomeContactDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Contact', function(Contact) {
                                return Contact.get({contactId : $stateParams.contactId}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('home', null, { reload: true });
                    }, function() {
                        $state.go('home');
                    });
                }]
            });
    }
})();
