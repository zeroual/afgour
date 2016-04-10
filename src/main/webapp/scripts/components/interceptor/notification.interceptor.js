 'use strict';

angular.module('afgourApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-afgourApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-afgourApp-params')});
                }
                return response;
            }
        };
    });
