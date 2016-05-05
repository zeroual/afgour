'use strict';

angular.module('afgourApp')
    .factory('IdentityRequestService', function ($http) {
        return {
            ask: function () {
                return $http.get("/identity/request/ask");
            },
            accept: function () {
                return $http.post("/identity/request/accept");
            }
        };
    });
