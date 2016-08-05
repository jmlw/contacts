// (function() {
//     'use strict';
//
//     angular
//         .module('contactsApp')
//         .config(stateConfig);
//
//     stateConfig.$inject = ['$stateProvider'];
//
//     function stateConfig($stateProvider) {
//         $stateProvider.state('home', {
//             parent: 'app',
//             url: '/',
//             data: {
//                 authorities: []
//             },
//             views: {
//                 'content@': {
//                     templateUrl: 'app/home/home.html',
//                     controller: 'HomeController',
//                     controllerAs: 'vm'
//                 },
//                 // 'login-view@home': {
//                 //     templateUrl: 'app/components/login/login-partial.html',
//                 //     controller: 'LoginController',
//                 //     controllerAs: 'vm'
//                 // },
//                 'contact-content@home': {
//                     templateUrl: 'app/entities/contact/contacts-home.html',
//                     controller: 'ContactController',
//                     controllerAs: 'vm'
//                 }
//             },
//             params: {
//                 page: {
//                     value: '1',
//                     squash: true
//                 },
//                 sort: {
//                     value: 'id,asc',
//                     squash: true
//                 },
//                 search: null
//             },
//             resolve: {
//                 pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
//                     return {
//                         page: PaginationUtil.parsePage($stateParams.page),
//                         sort: $stateParams.sort,
//                         predicate: PaginationUtil.parsePredicate($stateParams.sort),
//                         ascending: PaginationUtil.parseAscending($stateParams.sort),
//                         search: $stateParams.search
//                     };
//                 }],
//                 mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
//                     $translatePartialLoader.addPart('home');
//                     // $translatePartialLoader.addPart('login');
//                     $translatePartialLoader.addPart('contact');
//                     return $translate.refresh();
//                 }]
//             }
//         });
//     }
// })();
