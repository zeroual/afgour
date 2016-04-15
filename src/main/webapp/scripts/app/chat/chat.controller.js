'use strict';

angular.module('afgourApp')
    .controller('ChatController', function ($rootScope, $scope, $state,ChatService) {
        $scope.messagesList = [];
        $scope.message = {
            content: ''
        };

        $scope.$on('receivedMessageEvent', function (event, message) {
            $scope.messagesList.unshift(message);
            $scope.$apply();
        });

        $scope.sendMessage = function () {
            ChatService.sendMessage($scope.message);
            $scope.messagesList.unshift(angular.copy($scope.message));
            $scope.message.content = '';
        };
    });
