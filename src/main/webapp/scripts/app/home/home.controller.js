'use strict';

angular.module('afgourApp')
    .controller('HomeController', function ($scope, Principal, ChatService, $state,$http) {

        $scope.handshakeInProgress = false;

        $scope.members={
            onLine:1,
            total:1
        };

        $http.get('/chat/members').then(function (response) {
            $scope.members = response.data;
        });

        $scope.$on('receivedHandshakeEvent', function () {
            $state.go('chat');
        });

        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });

        $scope.askForHandshake = function () {
            $scope.handshakeInProgress = true;
            ChatService.askForHandshake();
        }
    });
