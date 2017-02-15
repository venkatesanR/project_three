(function() {
    'use strict';

    angular.module('projectThreeApp').factory('UrlService', ['$resource', 'appConfig', function($resource, appConfig) {

        /*
        createContractShell    : POST       : http://localhost:8080/cargopricewar/services/quote/createcontractshell
        search                 : POST       : http://localhost:8080/cargopricewar/services/quote/search
        getQuoteId             : GET        : http://localhost:8080/cargopricewar/services/quote/:carrier
        */

        return $resource('', {}, {
            findRoot: {
                method: 'POST',
                isArray: false,
                url: appConfig.serviceapi.FIND_ROOT_URL,
                timeout: 100000,
                globalErrorHandling: true,
                globalMsgHandling: true
            },
            getUserList: {
                method: 'GET',
                url: appConfig.serviceapi.GET_USER_LIST_URL,
                globalErrorHandling: true,
                globalMsgHandling: true
            }

        });
    }]);

    angular.module('projectThreeApp').factory('RootService', ['UrlService',
        function(UrlService) {
            return {
                resolve: function(request) {
                    var masterInfo = UrlService.findRoot(
                        request,
                        function(resData, responseHeaders) {
                            return resData;
                        });
                    return masterInfo.$promise;
                }
            };
        }
    ]);

    angular.module('projectThreeApp').factory('UserListService', ['UrlService',
        function(UrlService) {
            return {
                resolve: function(request) {
                    var masterInfo = UrlService.getUserList(
                        request,
                        function(resData, responseHeaders) {
                            return resData;
                        });
                    return masterInfo.$promise;
                }
            };
        }
    ]);


    /****************************************************************************/
    /*  HTML5 Web Storage : Browser storage. It stores without expiration date. */
    /****************************************************************************/
    angular.module('projectThreeApp').factory('aswLocalStorage', ['localStorage',
        function(localStorage) {
            return {
                getInstance: function(context) {
                    return {
                        watchAndStoreProperty: function($scope, storageName, defaultValue) {
                            var storageKey = this.getKey(storageName);
                            var json = localStorage[storageKey];
                            $scope[storageName] = json ? JSON.parse(json) : defaultValue;
                            $scope.$watch(
                                function() {
                                    return $scope[storageName];
                                },
                                function(value) {
                                    if (value) {
                                        localStorage[storageKey] = JSON.stringify(value);
                                    }
                                },
                                true);
                        },
                        readProperty: function(storageName) {
                            var storageKey = this.getKey(storageName);
                            var json = localStorage[storageKey];
                            if (angular.isDefined(json)) {
                                return JSON.parse(json);
                            }
                            return null;
                        },
                        setProperty: function(storageName, value) {
                            if (angular.isDefined(value)) {
                                var storageKey = this.getKey(storageName);
                                localStorage[storageKey] = JSON.stringify(value);
                            }
                        },
                        removeProperty: function(storageName) {
                            var storageKey = this.getKey(storageName);
                            localStorage.removeItem(storageKey);
                        },
                        clearAll: function() {
                            localStorage.clear();
                        },
                        getKey: function(storageName) {
                            return context + ':' + storageName;
                        }
                    };
                }
            };
        }
    ]);

    /******************************************************************************************/
    /*  HTML5 Web Storage : Browser session storage. Note each tab maintains it's own session */
    /******************************************************************************************/
    angular.module('projectThreeApp').factory('aswSession', ['sessionStorage',
        function(sessionStorage) {
            return {
                getInstance: function(context) {
                    return {
                        watchAndStoreProperty: function($scope, storageName, defaultValue) {
                            var storageKey = this.getKey(storageName);
                            var json = sessionStorage[storageKey];
                            $scope[storageName] = json ? JSON.parse(json) : jQuery.extend(true, {}, defaultValue);

                            $scope.$watch(
                                function() {
                                    return $scope[storageName];
                                },
                                function(value) {
                                    if (value) {
                                        sessionStorage[storageKey] = JSON.stringify(value);
                                    }
                                },
                                true);
                        },
                        readProperty: function(storageName) {
                            var storageKey = this.getKey(storageName);
                            var json = sessionStorage[storageKey];
                            if (angular.isDefined(json)) {
                                return JSON.parse(json);
                            }
                            return null;
                        },
                        setProperty: function(storageName, value) {
                            if (angular.isDefined(value)) {
                                var storageKey = this.getKey(storageName);
                                sessionStorage[storageKey] = JSON.stringify(value);
                            }
                        },
                        removeProperty: function(storageName) {
                            var storageKey = this.getKey(storageName);
                            sessionStorage.removeItem(storageKey);
                        },
                        clearAll: function() {
                            sessionStorage.clear();
                        },
                        getKey: function(storageName) {
                            return context + ':' + storageName;
                        }

                    };
                }
            };
        }
    ]);


    /*************************************/
    String.format = function() {
        // The string containing the format items (e.g. "{0}") will and always has to be the first argument.
        var theString = arguments[0];
        // start with the second argument (i = 1)
        for (var i = 1; i < arguments.length; i++) {
            // "gm" = RegEx options for Global search (more than one instance) and for Multiline search
            var regEx = new RegExp('\\{' + (i - 1) + '\\}', 'gm');
            theString = theString.replace(regEx, arguments[i]);
        }
        return theString;
    };


    /*************************************/

})();