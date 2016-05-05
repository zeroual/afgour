'use strict';

angular.module('afgourApp')
    .controller('ChatController', function ($rootScope, $scope, $state, ChatService) {


        // if (!ChatService.isHandshakeEstablished()) {
        //     redirectToHomePage();
        // }

        $scope.identityRequest = false;
        $scope.identityRequestResolved = false;

        $scope.messagesList = [];
        $scope.message = {
            content: '',
            from: "me"
        };
        $scope.partner = {
            image: '/assets/images/unknown.png',
            name: '',
            profileUrl: ''

        };
        $scope.$on('receivedMessageEvent', function (event, message) {
            $scope.messagesList.push(message);
            animateDisplayingMessage();
            $scope.$apply();
        });

        $rootScope.$on('identityRequestEvent', function () {
            $scope.identityRequest = true;
            $scope.$apply();
        });

        $rootScope.$on('identityResolvedEvent', function (event, identity) {
            $scope.identityRequestResolved = true;
            $scope.partner.image = identity.image;
            $scope.partner.name = identity.name;
            $scope.partner.facebook = identity.url;
            console.log('identity resolved', identity);
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

        $scope.sendShowIdentityRequest = function () {
            ChatService.askToShowIdentity();
        };

        $scope.acceptIdentityRequest = function () {
            ChatService.acceptToShowIdentity()
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
