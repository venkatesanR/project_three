(function() {
    'use strict';



    /* Filters */
    angular.module('projectThreeApp').filter('html', ['$sce', function($sce) {
        return function(text) {
            return $sce.trustAsHtml(text);
        };
    }]);

    angular.module('projectThreeApp').filter('YesNo', function() {
        return function(inputValue) {
            return inputValue ? 'Yes' : 'No';
        };
    });

    angular.module('projectThreeApp').filter('YN', function() {
        return function(inputValue) {
            return inputValue ? 'Y' : 'N';
        };
    });

    angular.module('projectThreeApp').filter('delimitedText', function() {
        return function(inputValue, delimiter) {
            if (angular.isUndefined(delimiter) || delimiter === '') {
                delimiter = ',';
            }
            if (angular.isArray(inputValue)) {
                return inputValue.join(delimiter);
            }
            return inputValue;
        };
    });

    angular.module('projectThreeApp').filter('isArray', function() {
        return function(input) {
            return angular.isArray(input);
        };
    });

    angular.module('projectThreeApp').filter('isObject', function() {
        return function(input) {
            return !angular.isArray(input);
        };
    });

    angular.module('projectThreeApp').filter('range', function() {
        return function(input, total) {
            total = parseInt(total);
            for (var i = 0; i < total; i++) {
                input.push(i);
            }
            return input;
        };
    });
    angular.module('projectThreeApp').filter('aswpager', function() {
        return function(input, config) {
            if (angular.isDefined(input) && input.length > 0) {
                var begin = (config.currentPage - 1) * config.itemsPerPage;
                var end = begin + config.itemsPerPage;
                if (end > config.totalItems) {
                    end = config.totalItems;
                }
                return input.slice(begin, end);
            }
            return input;
        };
    });


    // Added for spain team
    angular.module('projectThreeApp').filter('titleCase', function() {
        return function(input) {
            input = input || '';
            return input.replace(/_/g, ' ').replace(/\w\S*/g, function(txt) {
                return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            });
        };
    });
    angular.module('projectThreeApp').filter('titleCaseStatus', function() {
        return function(input) {
            input = input || '';
            return input.replace(/\w\S*/g, function(txt) {
                var index = txt.indexOf('_');
                if (index !== -1) {
                    var interStatus = txt.charAt(index + 1);
                    txt = txt.substring(0, 3);
                    return txt.charAt(0).toUpperCase() + txt.substr(1, 3).toLowerCase() + interStatus.toUpperCase() + '.';
                }
                return txt.charAt(0).toUpperCase() + txt.substr(1, 3).toLowerCase() + '.';
            });
        };
    });
    angular.module('projectThreeApp').filter('excludeIndices', function() {
        return function(input, excludeIndexList) {
            if (angular.isUndefined(excludeIndexList)) {
                return input;
            }
            var result = [];
            for (var i = 0; i < input.length; i++) {
                if (excludeIndexList.indexOf(input[i].originalIndex) === -1) {
                    result.push(input[i]);
                }
            }
            return result;
        };
    });

    angular.module('projectThreeApp').filter('aswTime', function() {
        /**
         * input should be a number of minutes to be parsed
         * @param {input} number of minutes
         * @param {type} true = 00:00:00 | false = 00:00 am or pm
         */
        return function(input, type) {
            input = (input % 1440);
            if (input < 0) {
                input = 1440 + input;
            }
            var hours = parseInt(input / 60, 10),
                minutes = (input - (hours * 60)) < 10 ? '0' + (input - (hours * 60)) : input - (hours * 60),
                meridian = type ? ':00' : (hours >= 12 && hours !== 24 ? ' pm' : ' am');
            return (!type && hours > 12 ? (hours === 24 ? '00' : (hours - 12 < 10 ? '0' : '') + (hours - 12)) : (hours < 10 ? '0' : '') + hours) + ':' + minutes + meridian;
        };
    });

})();