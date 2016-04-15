'use strict';

angular.module('afgourApp')
    .factory('ChatService', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscribers = [];
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

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
                stompClient.connect(headers, function (frame) {
                    connected.resolve("success");
                    // sendActivity();
                    if (!alreadyConnectedOnce) {
                        $rootScope.$on('$stateChangeStart', function (event) {
                            // sendActivity();
                        });
                        alreadyConnectedOnce = true;
                    }
                });
            },
            subscribe: function () {
                connected.promise.then(function () {
                    var subscriber = stompClient.subscribe("/user/queue/handshake", function (payload) {
                        $rootScope.$broadcast('receivedHandshakeEvent', JSON.parse(payload.body));
                    });
                    subscribers.push(subscriber);
                    subscriber = stompClient.subscribe("/user/queue/notifications", function (payload) {
                        $rootScope.$broadcast('receivedMessageEvent', JSON.parse(payload.body));
                        // listener.notify(JSON.parse(data.body));
                    });
                    subscribers.push(subscriber);
                }, null, null);
            },
            unsubscribe: function () {
                subscribers.forEach(function (subscriber) {
                    subscriber.unsubscribe()
                });
                listener = $q.defer();
            },
            receive: function () {
                return listener.promise;
            },
            sendMessage: function (message) {
                if (stompClient != null) {
                    sendMessage(message);
                }
            },
            disconnect: function () {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            },
            askForHandshake: function () {
                stompClient.subscribe("/app/handshake", function (payload) {
                    $rootScope.$broadcast('receivedHandshakeEvent', JSON.parse(payload.body));
                });
            }
        };
    });
