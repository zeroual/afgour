'use strict';

angular.module('afgourApp')
    .controller('ChatController', function ($rootScope, $scope, $state, ChatService) {


        if (!ChatService.isHandshakeEstablished()) {
            redirectToHomePage();
        }
        
        $scope.messagesList = [];
        $scope.message = {
            content: '',
            from: "me"
        };

        $scope.$on('receivedMessageEvent', function (event, message) {
            $scope.messagesList.push(message);
            animateDisplayingMessage();
            $scope.$apply();
        });

        $scope.sendMessage = function () {
            if ($scope.message.content) {
                ChatService.sendMessage($scope.message);
                $scope.messagesList.push(angular.copy($scope.message));
                $scope.message.content = '';
                animateDisplayingMessage();
            }
        };

        function animateDisplayingMessage() {
            $("#viewport-content").animate({
                bottom: $("#viewport-content").height() - $("#viewport").height() + 80
            }, 250);
        }

        function redirectToHomePage() {
            $state.go('home');
        }

    });
