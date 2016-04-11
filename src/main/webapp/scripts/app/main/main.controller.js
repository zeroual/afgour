'use strict';

angular.module('afgourApp')
    .controller('MainController', function ($scope, Principal,ChatService) {
        $scope.messagesList = [{message: 'hi'}];

        $scope.$on('receivedMessageEvent', function (event, message) {
            $scope.messagesList.push(message);
            $scope.$apply();
        });

        $scope.message = {
            to: '',
            message: ''
        };
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        ChatService.connect();
        ChatService.subscribe();
        $scope.sendMessage = function () {
            ChatService.sendMessage($scope.message);
        };
    });
