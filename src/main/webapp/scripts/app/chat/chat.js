'use strict';

angular.module('afgourApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('chat', {
                parent: 'site',
                url: '/chat',
                data: {
                    authorities: ['ROLE_USER'],
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/chat/chat.html',
                        controller: 'ChatController'
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
