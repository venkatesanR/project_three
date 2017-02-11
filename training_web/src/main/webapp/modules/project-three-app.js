(function() {
	'use strict';

	var projectThreeApp = angular.module('projectThreeApp', [
		'ngMessages',
		'ngAnimate',
		'ngRoute',
		'ngResource',
		'ui.bootstrap',
		'pascalprecht.translate',
		'ngSanitize',
		'ngFileUpload'
	]);

	/************************************************/
	/*Application parameters */
	/************************************************/
	projectThreeApp.value('appConfig', appParams);

	/************************************************/
	/*
		//Browser Storage 
		//window.localStorage - Browser storage. It stores without expiration date : window.localStorage.clear(); to clear storage manually. 
		//window.sessionStorage - Browser session storage. Note each tab maintains it's own session. : window.sessionStorage.clear(); to clear storage manually.
	*/

	/************************************************/
	projectThreeApp.value('localStorage', window.localStorage);
	projectThreeApp.value('sessionStorage', window.sessionStorage);

	/************************************************/
	/*Common Application RouteProvider Config */
	/************************************************/
	projectThreeApp.config(['$routeProvider',
		function($routeProvider) {
			$routeProvider.
			when('/workspace', {
				controller: 'MasterController',
				templateUrl: 'views/workspace.html'
			});
		}
	]);

})();