'use strict';

angular.module('afgourApp')
    .directive('getStarted', function($translatePartialLoader, $translate, $filter, SocialService) {
        return {
            restrict: 'E',
            scope: {
                provider: "@ngProvider"
            },
            templateUrl: 'scripts/app/account/social/directive/get-started.html',
            link: function(scope, element, attrs) {
                $translatePartialLoader.addPart('social');
                $translate.refresh();

                scope.label = $filter('capitalize')(scope.provider);
                scope.providerSetting = SocialService.getProviderSetting(scope.provider);
                scope.providerURL = SocialService.getProviderURL(scope.provider);
                scope.csrf = SocialService.getCSRF();
            }
        }
     });
