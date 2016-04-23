'use strict';

angular.module('afgourApp')
    .controller('HomeController', function ($scope, Principal, ChatService, $state) {

        $scope.handshakeInProgress = false;

        $scope.$on('receivedHandshakeEvent', function (event, user) {
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
