'use strict';

angular.module('afgourApp')
    .factory('ChatService', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscriber = null;
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
                stompClient.connect(headers, function(frame) {
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
            subscribe: function() {
                connected.promise.then(function() {
                    // subscriber = stompClient.subscribe("/topic/chat", function(payload) {
                    //     // $rootScope.$broadcast('receivedMessageEvent', JSON.parse(payload.body));
                    //     // listener.notify(JSON.parse(data.body));
                    // });
                    subscriber = stompClient.subscribe("/user/queue/notifications", function(payload) {
                        $rootScope.$broadcast('receivedMessageEvent', JSON.parse(payload.body));
                        // listener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
                listener = $q.defer();
            },
            receive: function() {
                return listener.promise;
            },
            sendMessage: function (message) {
                if (stompClient != null) {
                    sendMessage(message);
                }
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    });
