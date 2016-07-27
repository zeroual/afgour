'use strict';

angular.module('afgourApp')
    .controller('MainController', function ($scope, Principal, $timeout) {

        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });

        $scope.demoMessages = [];

        var $messages = $('.messages');

        $timeout(function () {
            $scope.demoMessages.push(
                {
                    text: 'Salut ! :)',
                    side: 'left'
                });
            $messages.animate({scrollTop: $messages.prop('scrollHeight')}, 300);
        }, 1000);

        $timeout(function () {
            $scope.demoMessages.push(
                {
                    text: 'Hi ! ça va ?',
                    side: 'right'
                });
            $messages.animate({scrollTop: $messages.prop('scrollHeight')}, 300);
        }, 2000);

        $timeout(function () {
            $scope.demoMessages.push(
                {
                    text: "Oui très bien , merci!",
                    side: 'left'
                });
            $messages.animate({scrollTop: $messages.prop('scrollHeight')}, 300);
        }, 3000);



        $timeout(function () {
            $scope.demoMessages.push(
                {
                    text: "hum, je suis curieux de savoir c ki :D ",
                    side: 'right'
                });
            $messages.animate({scrollTop: $messages.prop('scrollHeight')}, 300);
        }, 4000);
    });
