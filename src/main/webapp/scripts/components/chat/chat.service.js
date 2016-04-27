'use strict';

angular.module('afgourApp')
    .factory('ChatService', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var messageSubscriber;
        var handshakeSubscriber;
        var connected = $q.defer();
        var handshakeEstablished = false;

        function sendMessage(message) {
            if (stompClient != null && stompClient.connected) {
                stompClient
                    .send('/app/chat',
                        {},
                        JSON.stringify(message));
            }
        }

        $rootScope.$on('receivedHandshakeEvent', function () {
            handshakeEstablished = true;
        });

        //noinspection JSUnresolvedVariable
        return {
            connect: function () {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = window.location;
                var url = '//' + loc.host + loc.pathname + 'websocket/chat';
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                var headers = {};
                headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
                var deferred = $q.defer();
                stompClient.connect(headers, function () {
                    connected.resolve("success");
                    deferred.resolve('success');
                });
                return deferred.promise;
            },
            subscribeToHandshake: function () {
                connected.promise.then(function () {
                    handshakeSubscriber = stompClient.subscribe("/user/queue/handshake", function (payload) {
                        $rootScope.$broadcast('receivedHandshakeEvent', JSON.parse(payload.body));
                    });
                }, null, null);
            },
            subscribeToMessagesReceived: function () {
                connected.promise.then(function () {
                    messageSubscriber = stompClient.subscribe("/user/queue/notifications", function (payload) {
                        $rootScope.$broadcast('receivedMessageEvent', JSON.parse(payload.body));
                    });
                }, null, null);
            },
            disconnect: function () {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            },
            sendMessage: function (message) {
                if (stompClient != null) {
                    sendMessage(message);
                }
            },
            askForHandshake: function () {
                stompClient.subscribe("/app/handshake", function (payload) {
                    $rootScope.$broadcast('receivedHandshakeEvent', JSON.parse(payload.body));
                });
            },
            isHandshakeEstablished: function () {
                return handshakeEstablished;
            }
        };
    });
