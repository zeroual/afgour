'use strict';

angular.module('afgourApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


