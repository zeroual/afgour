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
                        var status = JSON.parse(payload.body);
                        if (status === 'STARTED') {
                            handshakeEstablished = true;
                            $rootScope.$broadcast('receivedHandshakeEvent', JSON.parse(payload.body));
                        } else {
                            handshakeEstablished = false;
                            $rootScope.$broadcast('handshakeLostEvent', JSON.parse(payload.body))
                        }
                    });
                }, null, null);
            },
            subscribeToMessagesReceived: function () {
                connected.promise.then(function () {
                    messageSubscriber = stompClient.subscribe("/user/queue/messages", function (payload) {
                        $rootScope.$broadcast('receivedMessageEvent', JSON.parse(payload.body));
                    });
                }, null, null);
            },
            subscribeToIdentityRequest: function () {
                connected.promise.then(function () {
                    messageSubscriber = stompClient.subscribe("/user/queue/identityRequest", function (payload) {
                        $rootScope.$broadcast('identityRequestEvent', JSON.parse(payload.body));
                    });
                }, null, null);
            },
            subscribeToIdentityResolved: function () {
                connected.promise.then(function () {
                    messageSubscriber = stompClient.subscribe("/user/queue/identityResolved", function (payload) {
                        $rootScope.$broadcast('identityResolvedEvent', JSON.parse(payload.body));
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
                return $http.get('/chat/handshake');
            },
            removeHandshake: function () {
                return $http.delete('/chat/handshake');
            },
            isHandshakeEstablished: function () {
                return handshakeEstablished;
            },
            isAlreadyConnected: function () {
                return stompClient != null && stompClient.connected
            }
        };
    });
