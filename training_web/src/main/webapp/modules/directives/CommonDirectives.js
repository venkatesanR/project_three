(function() {
	'use strict';

	/* Directives */
	/*
	    View html
	    <div app-messages></div>
	    <div asw-app-messages></div>
	    <div asw-app-messages excludes="['expiredDate']"></div>
	    <div asw-app-messages includes="['origin','destination','effectiveDate']"></div>
	    <div asw-app-messages includes="['origin','destination','effectiveDate']" excludes="['expiredDate']"></div>
	    
	    Specify the "controller" for asw-app-messages in $model html
	    <div asw-app-messages controller="CreateContractShellController"></div>
	*/
	angular.module('projectThreeApp').directive('aswAppMessages', ['$log', '$rootScope',
		function($log, $rootScope) {
			return {
				restrict: 'A',
				scope: {
					excludes: '=',
					includes: '='
				},
				templateUrl: 'appmessages.html',
				controller: function($scope, $element, $attrs) {
					//$log.debug('aswAppMessages : ' + $attrs.controller);
					
				},
				link: function($scope, $element, $attrs) {
					$element.on('$destroy', function() {
						$rootScope.controller = null;
					});
				}
			};
		}
	]);

})();