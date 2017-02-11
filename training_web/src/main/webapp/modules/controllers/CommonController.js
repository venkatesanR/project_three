(function() {
    'use strict';
    angular.module('projectThreeApp').controller('MasterController', ['$scope', '$locale', '$location', '$filter', '$translate', '$templateCache', '$routeParams', '$log', 'appConfig', 'MasterService',
        function MasterController($scope, $locale, $location, $filter, $translate, $templateCache, $routeParams, $log, appConfig,MasterService) {
            /*  Initialize  */
            $scope.pageName = $scope.pageName + '_' + editorMetaData.name;
            $log = $log.getInstance($scope.pageName);
           
            /*  Method(s) */
            function doSearch() {
                //$log.debug('doSearch');
                MasterTableService.search($scope.search, function(responseData, responseHeaders) {

                });
            }

            $scope.$on('$viewContentLoaded', function() {
                $log.debug('viewContentLoaded');
            });
        }
    ]);
})();