'use strict';

angular.module('afgourApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'site',
                url: '/home',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/home/home.html',
                        controller: 'HomeController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        return $translate.refresh();
                    }]
                }
            });
    });
