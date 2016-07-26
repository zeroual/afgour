'use strict';

angular.module('afgourApp')
    .controller('ChatController', function ($rootScope, $scope, $state, $uibModal, ChatService, IdentityRequestService) {


        if (!ChatService.isHandshakeEstablished()) {
            redirectToHomePage();
        }

        window.onbeforeunload = function (e) {
            return "Are you sure you want to navigate away from this page";
        };

        $rootScope.$on('$stateChangeStart', function (event) {
            if (!$scope.userConfirmToLeaveDiscution) {
                event.preventDefault();
                askUserForLeavingConfirmation().then(function () {
                    $scope.userConfirmToLeaveDiscution = true;
                    ChatService.removeHandshake().then(redirectToHomePage);
                });
            }
        });

        $scope.identityRequest = false;
        $scope.identityRequestResolved = false;

        $scope.messagesList = [];
        $scope.message = {
            content: '',
            from: "me"
        };
        $scope.partner = {
            image: '/assets/images/user.jpg',
            name: '',
            profileUrl: ''

        };
        $scope.$on('receivedMessageEvent', function (event, message) {
            $scope.messagesList.push(message);
            animateDisplayingMessage();
            $scope.$apply();
        });

        $scope.$on('handshakeLostEvent', function () {
            $scope.handshakeLost = true;
            $scope.$apply();
            animateDisplayingMessage();

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
            IdentityRequestService.ask();
            $scope.identityRequestSent = true;
        };

        $scope.acceptIdentityRequest = function () {
            IdentityRequestService.accept();
        };

        $scope.endHandshake = function () {
            redirectToHomePage();
        };

        function animateDisplayingMessage() {
            var $messages = $('.messages');
            $messages.animate({scrollTop: $messages.prop('scrollHeight')}, 300);
        }

        function redirectToHomePage() {
            $state.go('home');
        }

        function askUserForLeavingConfirmation() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'scripts/app/chat/confirm-end-handshake.html',
                controller: function ($scope, $uibModalInstance) {
                    $scope.ok = function () {
                        $uibModalInstance.close();
                    };

                    $scope.cancel = function () {
                        $uibModalInstance.dismiss('cancel');
                    };
                }
            });
            return modalInstance.result;
        }

    });
