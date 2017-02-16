(function() {
    'use strict';
    angular.module('projectThreeApp').controller('MasterController', ['$scope', 'MasterService',
        function MasterController($scope, MasterService) {
            /*  Initialize  */
            $scope.$on('$viewContentLoaded', function() {});
        }
    ]);
    angular.module('projectThreeApp').controller('MathsController', ['$scope', '$locale', '$location', '$filter', '$translate', '$templateCache', '$routeParams', 'appConfig', 'RootService','UrlService',
        function MathsController($scope, $locale, $location, $filter, $translate, $templateCache, $routeParams, appConfig, RootService,UrlService) {
            /*  Initialize  */
    		$scope.userInformationList = {};
            /*  Function(s) */
           $scope.findroot = function () {
        	   UrlService.findRoot($scope.req, function(responseData, responseHeaders) {
        		   $scope.userInformationList.x1 = responseData.x1;
        		   $scope.userInformationList.x2 = responseData.x2;
                    //$scope.userInformationList = responseData;
                });
            }      
            $scope.$on('$viewContentLoaded', function() {});
        }
    ]);
    angular.module('projectThreeApp').controller('UserListController', ['$scope', '$locale', '$location', '$filter', '$translate', '$templateCache', '$routeParams', 'appConfig', 'UserListService',
        function UserListController($scope, $locale, $location, $filter, $translate, $templateCache, $routeParams, appConfig, UserListService) {
            /*  Initialize  */
            $scope.filterOptions = {
                filterText: ''
            };

            $scope.gridOptions = {
                data: 'userInformationList',
                filterOptions: $scope.filterOptions
            };

            $scope.filterName = function() {
                if (angular.isDefined($scope.name) && $scope.name !== null) {
                    $scope.filterOptions.filterText = $scope.name;
                } else if (angular.isDefined($scope.origin) && $scope.origin !== null) {
                    $scope.filterOptions.filterText = $scope.origin;
                } else if (angular.isDefined($scope.destination) && $scope.destination !== null) {
                    $scope.filterOptions.filterText = $scope.destination;
                }
            };

            /*  Function(s) */

            function searchByName() {
                MasterTableService.search($scope.search, function(responseData, responseHeaders) {
                    $scope.userInformationList = responseData;
                });
            }

            $scope.$on('$viewContentLoaded', function() {});
        }
    ]);
})();