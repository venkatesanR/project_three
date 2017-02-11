(function() {
	'use strict';
	var ASC = 'asc';
	var DESC = 'desc';
	var GROUP_DEPTH = '_depth';
	var GROUP_NAME = '_group';
	var ASW_GRID_BODY = /ASW_GRID_BODY/g;
	var RENDER_COLS = /RENDER_COLS/g;
	var RENDER_HEADER_ROWS = /RENDER_HEADER_ROWS/g;
	var ASW_ROW_TYPE = /ASW_ROW_TYPE/g;
	var NO_RECORD_FOUND_MSG = /NO_RECORD_FOUND_MSG/g;
	var ASW_GRID_GROUPPANEL = /ASW_GRID_GROUPPANEL/g;
	var COL_SPAN = /COL_SPAN/g;
	var COL_FIELD = /COL_FIELD/g;
	var COL_TOOLTIP = /COL_TOOLTIP/g;
	var CUSTOM_FILTERS = /CUSTOM_FILTERS/g;
	var APOS_REGEXP = /'/g;
	var DOT_REGEXP = /\./g;
	var FUNC_REGEXP = /(\([^)]*\))?$/;
	var BRACKET_REGEXP = /^(.*)((?:\s*\[\s*\d+\s*\]\s*)|(?:\s*\[\s*"(?:[^"\\]|\\.)*"\s*\]\s*)|(?:\s*\[\s*'(?:[^'\\]|\\.)*'\s*\]\s*))(.*)$/;
	var iso8601RegEx = /^(\d{4}|\+\d{6})(?:-(\d{2})(?:-(\d{2})(?:T(\d{2}):(\d{2}):(\d{2})\.(\d{1,})(Z|([\-+])(\d{2}):(\d{2}))?)?)?)?$/;

	var defaultGridOptions = {
		maxWidth: 'auto',
		totalRows: 0,
		enablePaging: false,
		enableFiltering: false,
		enableGrouping: false,
		enablePinning: false,
		groupByExpanded: false,
		filterOptions: {},
		pagingOptions: {
			pageSizes: [10, 25, 50, 75, 100],
			pageSize: 25,
			currentPage: 1
		},
		headerGroupDefs: {},
		enableSorting: true,
		enableColumnReordering: false,
		enableColumnResize: false,
		useExternalSorting: false,
		sortInfo: [],
		sorted: false,
		showFooter: false,
		showHeading: true,
		showColumnMenu: false,
		enableRowSelection: true,
		multiSelect: false,
		selectedItems: [],
		afterSelectionChange: function(selectedItems) {},
		showNoRecordFoundMsg: false
	};

	var defaultHeaderFilter = {
		fieldType: 'text', // 'text' or 'date' or 'num'
		valueType: 'SingleValue', // 'SingleValue' or CustomValues or MultiValues
		filtered: false,
		enableFiltering: true
	};

	var defaultEntityUI = {
		expanded: true,
		expandedChildren: false,
		expandedDetail: false
	};

	angular.module('cargocommonApp').directive('aswGrid', ['$log', '$compile', '$animate', '$timeout', '$templateCache', '$filter', 'gridUtils', 'gridSortUtils', 'aswPagingOptions', 'aswOperatorUtils', 'aswCalendar',
		function($log, $compile, $animate, $timeout, $templateCache, $filter, gridUtils, gridSortUtils, aswPagingOptions, aswOperatorUtils, aswCalendar) {
			return {
				scope: true,
				controller: function($scope, $element, $attrs) {
					//$log.debug('aswGrid:controller');
					$scope.calendar = aswCalendar.config();
				},
				link: function($scope, $element, $attrs) {
					//$log.debug('aswGrid:link');
					var createASWGrid = function(options) {
						var self = {};
						self.gridId = 'asw-' + gridUtils.newId();
						self.gridClassId = 'aswGrid ' + self.gridId;
						self.gridGroupPanelId = 'group-' + self.gridId;
						self.gridBodyId = 'body-' + self.gridId;

						self.config = jQuery.extend({}, defaultGridOptions, options);
						self.data = [];
						self.columnDefs = [];
						self.showHeader = true;
						self.rowIndex = 0;
						self.grouping = false;
						self.groupedRows = [];
						self.parentCache = [];
						self.aswAggRowRef = {};
						self.aswRowRef = {};
						self.scheduleEvents = {};

						self.initialize = function() {
							//$log.debug('initialize');
							$scope.gridId = self.gridId;
							$scope.columns = [];
							$scope.renderHeaderRows = [];
							$scope.renderCols = [];
							$scope.renderPinnedHeaderRows = [];
							$scope.renderPinnedCols = [];
							$scope.sortInfo = self.config.sortInfo;
							$scope.renderGroupCols = [];
							$scope.renderRows = [];
							$scope.totalRows = 0;
							$scope.enablePaging = self.config.enablePaging;
							$scope.pagingOptions = self.config.pagingOptions;
							$scope.filterOptions = self.config.filterOptions;
							$scope.showHeading = self.config.showHeading;
							$scope.enableSorting = self.config.enableSorting;
							$scope.enableGrouping = self.config.enableGrouping;
							$scope.enablePinning = self.config.enablePinning;
							$scope.enableFiltering = self.config.enableFiltering;
							$scope.enableColumnResize = self.config.enableColumnResize;
							$scope.showFooter = self.config.showFooter;
							$scope.showColumnMenu = self.config.showColumnMenu;
							$scope.selectedItems = gridUtils.isNullOrUndefined(self.config.selectedItems) ? [] : self.config.selectedItems;
							$scope.showNoRecordFoundMsg = !gridUtils.isNullOrUndefined(self.config.noRecordFoundMsg);

							if ($scope.enableGrouping) {
								$scope.groupBy = gridUtils.isNullOrUndefined(self.config.groupBy) ? [] : self.config.groupBy;

							} else {
								$scope.groupBy = [];
							}

							if ($scope.showNoRecordFoundMsg) {
								$scope.noRecordFoundMsg = self.config.noRecordFoundMsg;
							} else {
								$scope.showNoRecordFoundMsg = self.config.showNoRecordFoundMsg;
								$scope.noRecordFoundMsg = '';
							}

							/* Watch columnDefs */
							if (typeof self.config.columnDefs === 'string') {
								$scope.$parent.$watchCollection(self.config.columnDefs, function(newValue) {
									if (newValue) {
										self.columnDefs = newValue;
										self.buildColumns();
									}
								});
							} else {
								self.columnDefs = self.config.columnDefs;
								self.buildColumns();
							}

							/* Watch totalRows */
							if (typeof self.config.totalRows === 'string') {
								$scope.$parent.$watchCollection(self.config.totalRows, function(newValue, oldValue) {
									$scope.totalRows = angular.isDefined(newValue) ? newValue : 0;
								});
							} else {
								$scope.totalRows = self.config.totalRows;
							}

							$scope.dataInitialized = false;

							/* Watch data */
							if (typeof self.config.data === 'string') {
								$scope.$parent.$watchCollection(self.config.data, function(newValue) {
									if (newValue) {
										if (!angular.isArray(newValue) && newValue.initialize) {
											$scope.selectedItems = [];

											if (self.config.enableFiltering) {
												self.buildMultiValues();
											}
											//Filtering,Sorting,Grouping
											$scope.$emit('aswGridEvent', {
												gridId: self.gridId,
												eventType: 'ApplyFiltering'
											});

										} else if ((!angular.isArray(newValue) && newValue.filtering) || (!angular.isArray(newValue) && newValue.grouping)) {
											self.data = newValue.data;
											$scope.$emit('aswGridEvent', {
												gridId: self.gridId,
												eventType: 'RenderRows'
											});
										} else if (angular.isArray(newValue)) {
											self.data = newValue;
											$scope.$emit('aswGridEvent', {
												gridId: self.gridId,
												eventType: 'RenderRows'
											});
										}
										$scope.dataInitialized = true;
									}
								});
							} else {
								self.data = self.config.data;
								$scope.$emit('aswGridEvent', {
									gridId: self.gridId,
									eventType: 'RenderRows'
								});
								$scope.dataInitialized = true;
							}
							if (typeof self.config.data === 'string' && self.config.enableFiltering) {
								$scope.$watch(self.config.data + 'FilterOptions', function(newVal, oldVal) {
									if (newVal !== oldVal) {
										//$log.debug('Watch : FilterOptions ' + JSON.stringify(newVal));
										$scope.filterOptions = newVal;
										$scope.$emit('aswGridEvent', {
											gridId: self.gridId,
											eventType: 'ApplyFiltering'
										});
									}
								}, true);
							}

							$scope.$watchCollection('columns', function(newVal, oldVal) {
								//$log.debug('$watch : columns');
								self.buildRenderColumns();
							});

							var uKey;
							if (angular.isDefined(options.moreActionTemplate)) {
								uKey = self.gridId + 'moreActionTemplate.html';
								$templateCache.put(uKey, $templateCache.get(options.moreActionTemplate));
							}
							if (angular.isDefined(options.settingsTemplate)) {
								uKey = self.gridId + 'settingsTemplate.html';
								$templateCache.put(uKey, $templateCache.get(options.settingsTemplate));
							}
							if (angular.isDefined(options.rowContextmenuTemplate)) {
								uKey = self.gridId + 'rowContextmenuTemplate.html';
								$scope.rowContextmenuTemplate = uKey;
								$templateCache.put(uKey, $templateCache.get(options.rowContextmenuTemplate));
							}
							if (angular.isDefined(options.rowDetailTemplate)) {
								uKey = self.gridId + 'rowDetailTemplate.html';
								$templateCache.put(uKey, $templateCache.get(options.rowDetailTemplate));
							}
						};
						self.buildMultiValues = function() {
							//$log.debug('buildMultiValues');
							var multiValues = {};
							angular.forEach($scope.columns, function(aswCol, index) {
								if (aswCol.headerFilter.valueType === 'MultiValues') {
									multiValues[aswCol.field] = [];
								}
							});
							var props = Object.keys(multiValues);
							var dataCopy = self.config.data + 'Copy';
							var data = $scope.$parent[dataCopy];
							if (data && data.length > 0) {
								var model;
								var val;
								var prop;
								for (var x = 0; x < data.length; x++) {
									model = data[x];
									for (var y = 0; y < props.length; y++) {
										prop = props[y];
										val = model[prop];
										if (val !== null) {
											if (multiValues[prop].indexOf(val) === -1) {
												multiValues[prop].push(val);
											}
										}
									}
								}
							}
							angular.forEach($scope.columns, function(aswCol, index) {
								if (aswCol.headerFilter.valueType === 'MultiValues') {
									aswCol.headerFilter.values = multiValues[aswCol.field];
								}
							});
						};
						self.buildColumns = function() {
							//$log.debug('buildColumns');
							var columns = [];
							var aswCol;
							var criteria = [];
							var colCriteria;
							if (self.columnDefs.length > 0) {
								if (self.config.multiSelect) {
									var columnDef = {
										headerCellTemplate: $templateCache.get('checkboxHeaderTemplate.html'),
										cellTemplate: $templateCache.get('checkboxCellTemplate.html'),
										pinned: self.config.enablePinning
									};
									self.columnDefs.unshift(columnDef);
								}
								angular.forEach(self.columnDefs, function(columnDef, index) {
									columnDef.originalIndex = gridUtils.isNullOrUndefined(columnDef.originalIndex) ? index : columnDef.originalIndex;
									aswCol = createASWCol(columnDef, index, self);
									if (self.config.enableFiltering && aswCol.headerFilter.enableFiltering) {
										colCriteria = {
											property: aswCol.field,
											operator: '=',
											value: ''
										};
										if (aswCol.headerFilter.valueType === 'SingleValue') {
											aswCol.headerFilter.operators = aswOperatorUtils.getOperatorsByType([], aswCol.headerFilter.fieldType);
										}
										aswCol.headerFilter.criteriaIndex = criteria.length;
										criteria.push(colCriteria);
									}
									columns.push(aswCol);
								});
							}
							$scope.columns = columns;
							$scope.filterOptions.criteria = criteria;
						};
						self.orderColumnsByHeaderGroup = function(columns) {
							var orderedColumns = [];
							var headerGroup;
							var uniqueIndex = [];
							angular.forEach(columns, function(column, index) {
								if (uniqueIndex.indexOf(index) === -1) {
									if (angular.isDefined(column.headerGroup)) {
										headerGroup = column.headerGroup;
										for (var i = index; i < columns.length; i++) {
											if (headerGroup === columns[i].headerGroup) {
												orderedColumns.push(columns[i]);
												uniqueIndex.push(i);
											}
										}
									} else {
										orderedColumns.push(column);
										uniqueIndex.push(index);
									}
								}
							});
							return orderedColumns;
						};
						self.buildRenderColumns = function() {
							//$log.debug('buildRenderColumns');
							var renderPinnedCols = [];
							var renderCols = [];
							var renderGroupCols = [];

							var fieldIndex = {};
							var headerDetail = {};
							var headerGroup;
							var hasHeaderGroupInPinnedCols = false;
							var hasHeaderGroupInCols = false;
							var aswGroupCell;

							angular.forEach($scope.columns, function(column, index) {
								if (angular.isDefined(column.headerGroup)) {
									headerGroup = column.headerGroup;
									if (!headerDetail[headerGroup]) {
										headerDetail[headerGroup] = {
											fieldIndices: [],
											visibleFieldIndices: []
										};
									}
									headerDetail[headerGroup].fieldIndices.push(index);
								}
								column.isGroupedBy = angular.isDefined(column.field) && $scope.groupBy.indexOf(column.field) !== -1;
								if (column.visible && !column.isGroupedBy) {
									if (angular.isDefined(column.headerGroup)) {
										headerGroup = column.headerGroup;
										if (headerDetail[headerGroup]) {
											headerDetail[headerGroup].visibleFieldIndices.push(index);
										}
										if (column.pinned) {
											hasHeaderGroupInPinnedCols = true;
										} else {
											hasHeaderGroupInCols = true;
										}
									}
									if (column.pinned) {
										renderPinnedCols.push(column);
									} else {
										renderCols.push(column);
									}
								}
								if (angular.isDefined(column.field)) {
									fieldIndex[column.field] = index;
								}
							});

							//Default first column as pinned if no column is pinned.	
							if ($scope.enablePinning && renderPinnedCols.length === 0) {
								for (var i = 0; i < renderCols.length; i++) {
									if (renderCols[i].pinnable) {
										renderCols[i].pinned = true;
										if (angular.isDefined(renderCols[i].headerGroup)) {
											hasHeaderGroupInPinnedCols = true;
										}
										renderPinnedCols.push(renderCols[i]);
										renderCols.splice(i, 1);
										break;
									}
								}
							}

							angular.forEach($scope.groupBy, function(field, index) {
								renderGroupCols.push($scope.columns[fieldIndex[field]]);
								aswGroupCell = createASWGroupCell(index);
								renderCols.unshift(aswGroupCell);
							});

							$scope.hasHeaderGroup = (hasHeaderGroupInPinnedCols || hasHeaderGroupInCols);
							renderPinnedCols = self.orderColumnsByHeaderGroup(renderPinnedCols);
							renderCols = self.orderColumnsByHeaderGroup(renderCols);

							var renderPinnedHeaderRows = self.buildRenderHeaderRows(hasHeaderGroupInPinnedCols, renderPinnedCols, headerDetail);
							var renderHeaderRows = self.buildRenderHeaderRows(hasHeaderGroupInCols, renderCols, headerDetail);

							$scope.renderPinnedHeaderRows = renderPinnedHeaderRows;
							$scope.renderPinnedCols = renderPinnedCols;
							$scope.renderHeaderRows = renderHeaderRows;
							$scope.renderCols = renderCols;
							$scope.renderGroupCols = renderGroupCols;
							$scope.hasPinnedCols = ($scope.enablePinning && $scope.renderPinnedCols.length > 0);
							if (self.config.showHeading) {
								self.scheduleEvent('setJQueryUIWidgets', 300, function() {
									self.setJQueryUIWidgets();
								});
								self.scheduleEvent('fixedHeaderTable', 300, function() {
									self.fixedHeaderTable();
								});
							}
						};
						self.buildRenderHeaderRows = function(hasHeaderGroupInCols, renderCols, headerDetail) {
							var renderHeaderRows;
							if ($scope.hasHeaderGroup) {
								//Calculate colSpan
								var headerGroup;
								var headerGroups = Object.keys(headerDetail);
								for (var i = 0; i < headerGroups.length; i++) {
									headerDetail[headerGroups[i]].colSpan = 0;
								}
								angular.forEach(renderCols, function(column, index) {
									if (angular.isDefined(column.headerGroup)) {
										headerGroup = column.headerGroup;
										if (headerDetail[headerGroup]) {
											headerDetail[headerGroup].colSpan++;
										}
									}
								});
								renderHeaderRows = new Array([], []);
								var aswHeaderGroup;
								if (hasHeaderGroupInCols) {
									var headerGroupNames = [];
									angular.forEach(renderCols, function(renderCol, index) {
										if (angular.isDefined(renderCol.headerGroup)) {
											headerGroup = renderCol.headerGroup;
											if (headerGroupNames.indexOf(headerGroup) === -1) {
												headerGroupNames.push(headerGroup);
												aswHeaderGroup = createASWHeaderGroup(renderCol, headerDetail[headerGroup], self);
												renderHeaderRows[0].push(aswHeaderGroup);
												renderHeaderRows[1].push(renderCol);
											} else {
												renderHeaderRows[1].push(renderCol);
											}
										} else {
											renderCol.rowSpan = 2;
											renderHeaderRows[0].push(renderCol);
										}
									});
								} else {
									aswHeaderGroup = {
										rowSpan: 1,
										colSpan: renderCols.length,
										index: 444,
										headerGroup: '',
										isGroupHeader: true,
										headerClass: aswHeaderGroup,
										isAggCol: false
									};
									renderHeaderRows[0].push(aswHeaderGroup);
									jQuery.merge(renderHeaderRows[1], renderCols);
								}
							} else {
								renderHeaderRows = [];
								renderHeaderRows.push(renderCols);
							}
							return renderHeaderRows;
						};
						self.fixColumnIndexes = function() {
							//Fix column indexes
							for (var i = 0; i < $scope.columns.length; i++) {
								$scope.columns[i].index = i;
							}
						};
						self.findColumn = function(originalIndex) {
							var columns = $scope.columns;
							for (var i = 0; i < columns.length; i++) {
								if (columns[i].originalIndex === originalIndex) {
									return columns[i];
								}
							}
							return null;
						};
						self.setCustomWidth = function(originalIndex, width) {
							self.findColumn(originalIndex).customWidth = width;
						};
						self.getScrollbarWidth = function() {
							var scrollbarWidth = 0;
							if (!scrollbarWidth) {
								if (/msie/.test(navigator.userAgent.toLowerCase())) {
									var $textarea1 = $('<textarea cols="10" rows="2"></textarea>')
										.css({
											position: 'absolute',
											top: -1000,
											left: -1000
										}).appendTo('body'),
										$textarea2 = $('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>')
										.css({
											position: 'absolute',
											top: -1000,
											left: -1000
										}).appendTo('body');

									scrollbarWidth = $textarea1.width() - $textarea2.width() + 2; // + 2 for border offset
									$textarea1.add($textarea2).remove();
								} else {
									var $div = $('<div />')
										.css({
											width: 100,
											height: 100,
											overflow: 'auto',
											position: 'absolute',
											top: -1000,
											left: -1000
										})
										.prependTo('body').append('<div />').find('div')
										.css({
											width: '100%',
											height: 200
										});

									scrollbarWidth = 100 - $div.width();
									$div.parent().remove();
								}
							}
							return scrollbarWidth;
						};

						self.syncTableWidthHeight = function() {
							var i;
							if ($scope.hasPinnedCols) {
								var maxHeight, tableLeftTR, tableRightTR;
								var tableLeftTRs = self.$gridBody.find('.asw-pinned-column .asw-thead .aswtable thead tr.asw-row, .asw-pinned-column .asw-tbody .aswtable thead tr.asw-row, .asw-pinned-column .asw-tbody .aswtable tbody tr.asw-row');
								var tableRightTRs = self.$gridBody.find('.asw-pinned-body .asw-thead .aswtable thead tr.asw-row, .asw-pinned-body .asw-tbody .aswtable thead tr.asw-row, .asw-pinned-body .asw-tbody .aswtable tbody tr.asw-row');
								//$log.debug('syncTableWidthHeight : ' + tableLeftTRs.length + ' : ' + tableRightTRs.length);
								for (i = 0; i < tableLeftTRs.length; i++) {
									tableLeftTR = $(tableLeftTRs[i]);
									tableRightTR = $(tableRightTRs[i]);
									maxHeight = Math.max(tableLeftTR.height(), tableRightTR.height());
									tableLeftTR.css({
										'height': maxHeight
									});
									tableRightTR.css({
										'height': maxHeight
									});
								}
							}
							//Prevent wrap.
							/*
							var aswHeaderCells = $gridBody.find('.asw-tbody .aswtable .aswHeader .aswHeaderCell');
							aswHeaderCells.each(function() {
								$log.debug('outerWidth = ' + $(this).outerWidth());
								$(this).css('width', $(this).outerWidth());
							});
							*/
							var thHead = self.$gridBody.find('.asw-thead .aswtable .aswHeaderCell');
							var thBody = self.$gridBody.find('.asw-tbody .aswtable .aswHeader');
							if (thHead.length === thBody.length) {
								for (i = 0; i < thBody.length; i++) {
									$(thHead[i]).css({
										'width': $(thBody[i]).width()
									});
								}
							}
						};
						self.scheduleEvent = function(fnName, pause, callback) {
							if (self.scheduleEvents[fnName]) {
								$timeout.cancel(self.scheduleEvents[fnName]);
							}
							self.scheduleEvents[fnName] = $timeout(function() {
								callback();
							}, pause);
						};
						self.fixedHeaderTable = function() {
							//$log.debug('fixedHeaderTable');
							self.syncTableWidthHeight();
							var settings = {};
							settings.borderCollapse = 1;
							settings.scrollbarOffset = self.getScrollbarWidth();
							settings.widthMinusScrollbar = Math.min(self.$gridBody[0].offsetWidth, self.$gridBody.width()) - settings.scrollbarOffset;
							var $wrapper = self.$gridBody.find('.asw-table-wrapper');
							var $divHead, $headTable, $divBody, $bodyTable;
							if ($scope.hasPinnedCols) {
								$divHead = self.$gridBody.find('.asw-thead');
								$divBody = self.$gridBody.find('.asw-tbody');
								$bodyTable = $divBody.find('.aswtable');

								var $divPinnedCol = self.$gridBody.find('.asw-pinned-column');
								var $divPinnedColHead = $divPinnedCol.find('.asw-thead');
								var $headPinnedColTable = $divPinnedColHead.find('.aswtable');
								var $divPinnedColBody = $divPinnedCol.find('.asw-tbody');
								var $bodyPinnedColTable = $divPinnedColBody.find('.aswtable');

								var $divPinned = self.$gridBody.find('.asw-pinned-body');
								var $divPinnedHead = $divPinned.find('.asw-thead');
								var $headPinnedTable = $divPinnedHead.find('.aswtable');
								var $divPinnedBody = $divPinned.find('.asw-tbody');
								var $bodyPinnedTable = $divPinnedBody.find('.aswtable');
								$divPinnedColBody.css({
									'height': ''
								});
								$divPinnedBody.css({
									'height': ''
								});
								settings.divHeadHeight = $divHead.outerHeight(true);
								settings.bodyTableBorder = ($bodyTable.find('th:first-child').outerWidth() - $bodyTable.find('th:first-child').innerWidth()) / settings.borderCollapse;
								settings.divBodyAdjHeight = (settings.divHeadHeight + settings.bodyTableBorder + settings.scrollbarOffset);
								settings.divBodyHeight = Math.min(self.$gridBody[0].offsetHeight, $wrapper.height()) - settings.divBodyAdjHeight;
								settings.bodyHasHScroll = ($divPinnedBody[0].scrollWidth > $divPinnedBody[0].offsetWidth);
								settings.bodyHasVScroll = ($divPinnedBody[0].scrollHeight > settings.divBodyHeight);

								settings.pinnedColumnWidth = $bodyPinnedColTable.width();
								//$log.debug(self.config.data + ' : settings : ' + JSON.stringify(settings));
								if (settings.widthMinusScrollbar > 0 && settings.pinnedColumnWidth > 0) {
									var headPinnedTableWidth;
									if (settings.bodyHasVScroll) {
										headPinnedTableWidth = settings.widthMinusScrollbar - settings.pinnedColumnWidth - settings.scrollbarOffset;
									} else {
										headPinnedTableWidth = settings.widthMinusScrollbar - settings.pinnedColumnWidth;
									}
									var bodyPinnedTableWidth = settings.widthMinusScrollbar - settings.pinnedColumnWidth;
									if (bodyPinnedTableWidth < 300) {
										bodyPinnedTableWidth = 300;
										headPinnedTableWidth = bodyPinnedTableWidth - settings.scrollbarOffset;

										$headPinnedColTable.css({
											'width': settings.pinnedColumnWidth - bodyPinnedTableWidth
										});
										$bodyPinnedColTable.css({
											'width': settings.pinnedColumnWidth - bodyPinnedTableWidth
										});
										$divPinnedCol.css({
											'width': settings.pinnedColumnWidth - bodyPinnedTableWidth
										});
										$divPinnedColBody.css({
											'overflow-x': 'auto',
											'overflow-y': 'hidden'
										});
										/* Bind scroll */
										$divPinnedColBody.bind('scroll', function() {
											$headPinnedColTable.css({
												'margin-left': -this.scrollLeft
											});
										});
									} else {
										$divPinnedColBody.css({
											'overflow-x': 'hidden',
											'overflow-y': 'hidden'
										});
									}
									$headPinnedTable.css({
										'width': headPinnedTableWidth
									});
									$bodyPinnedTable.css({
										'width': bodyPinnedTableWidth
									});
									$divPinned.css({
										'width': bodyPinnedTableWidth
									});
								}

								if (settings.divBodyHeight > 100) {
									var divPinnedColBodyHeight;
									if (settings.bodyHasHScroll) {
										divPinnedColBodyHeight = settings.divBodyHeight - settings.scrollbarOffset;
									} else {
										divPinnedColBodyHeight = settings.divBodyHeight;
									}

									$divPinnedColBody.css({
										'height': divPinnedColBodyHeight
									});
									$divPinnedBody.css({
										'height': settings.divBodyHeight
									});
								}

								$bodyTable.css({
									'margin-top': -settings.divHeadHeight
								});

								/* Bind scroll */
								$divPinnedBody.bind('scroll', function() {
									$bodyPinnedColTable.css({
										'margin-top': -this.scrollTop - settings.divHeadHeight
									});
									$headPinnedTable.css({
										'margin-left': -this.scrollLeft
									});
								});
							} else {
								$divHead = self.$gridBody.find('.asw-thead');
								$headTable = $divHead.find('.aswtable');
								$divBody = self.$gridBody.find('.asw-tbody');
								$bodyTable = $divBody.find('.aswtable');

								$divBody.css({
									'height': ''
								});

								settings.divHeadHeight = $divHead.outerHeight(true);
								settings.bodyTableBorder = ($bodyTable.find('th:first-child').outerWidth() - $bodyTable.find('th:first-child').innerWidth()) / settings.borderCollapse;
								settings.divBodyAdjHeight = (settings.divHeadHeight + settings.bodyTableBorder + settings.scrollbarOffset);
								settings.divBodyHeight = Math.min(self.$gridBody[0].offsetHeight, $wrapper.height()) - settings.divBodyAdjHeight;

								//$log.debug(self.config.data + ' : settings : ' + JSON.stringify(settings));

								if (settings.widthMinusScrollbar > 0) {
									$headTable.css({
										'width': settings.widthMinusScrollbar
									});
									$bodyTable.css({
										'width': settings.widthMinusScrollbar
									});
								}
								if (settings.divBodyHeight > 100) {
									$divBody.css({
										'height': settings.divBodyHeight
									});
								}
								$bodyTable.css({
									'margin-top': -settings.divHeadHeight
								});
								/* Bind scroll */
								$divBody.bind('scroll', function() {
									$headTable.css({
										'margin-left': -this.scrollLeft
									});
								});
							}
							self.syncTableWidthHeight();
							self.setBodyVisibility(true);
						};
						self.setJQueryUIWidgets = function() {
							//$log.debug('setJQueryUIWidgets');
							if (self.config.enableColumnReordering) {
								var $aswHeaders = self.$gridBody.find('.asw-thead .aswHeader');
								$aswHeaders.bind('selectstart', function() {
									return false;
								});
								var aswHeader;
								var tops = {};
								//$log.debug('$aswHeaders : ' + $aswHeaders.length);
								$aswHeaders.each(function(index) {
									aswHeader = $(this);
									if (!aswHeader.data('ui-draggable') && !aswHeader.children().hasClass('aswGroupCell')) {
										aswHeader.draggable({
											addClasses: false,
											zIndex: 3,
											scroll: false,
											start: function(event, ui) {
												tops.positionTop = ui.position.top;
												var aswHeader = $(event.target).closest('.aswHeader');
												var headerScope = angular.element(aswHeader).scope();
												if (headerScope) {
													self.colMoveFrom = {
														index: headerScope.col.index,
														groupable: headerScope.col.groupable,
														headerGroup: headerScope.col.headerGroup,
														isGroupHeader: headerScope.col.isGroupHeader
													};
												}
											},
											drag: function(event, ui) {
												ui.position.top = tops.positionTop;
											},
											revert: function() {
												$timeout(function() {
													self.colMoveFrom = undefined;
												}, 50);
												return true;
											}
										}).droppable({
											addClasses: false,
											hoverClass: 'highlightDrop',
											drop: function(event, ui) {
												if (!self.colMoveFrom) {
													return;
												}
												var aswHeader = $(event.target).closest('.aswHeader');
												var headerScope = angular.element(aswHeader).scope();
												if (headerScope) {
													var colMoveTo = {
														index: headerScope.col.index,
														groupable: headerScope.col.groupable,
														isGroupHeader: headerScope.col.isGroupHeader,
														headerGroup: headerScope.col.headerGroup,
														headerFieldIndices: headerScope.col.headerVisibleFieldIndices
													};
													if (self.colMoveFrom.index === colMoveTo.index) {
														return;
													}
													var i, column;
													if (self.colMoveFrom.isGroupHeader || colMoveTo.isGroupHeader) {
														var getColumns = function(indexValues) {
															var columns = [];
															for (i = 0; i < indexValues.length; i++) {
																columns.push($scope.columns[indexValues[i]]);
															}
															return columns;
														};
														var columns, indexValues1, indexValues2, index;
														if (self.colMoveFrom.isGroupHeader && colMoveTo.isGroupHeader) {
															indexValues1 = self.colMoveFrom.headerFieldIndices;
															indexValues2 = colMoveTo.headerFieldIndices;
															index = indexValues2[0] + indexValues2.length;
															if (indexValues2[0] > indexValues1[0]) {
																index = index - indexValues1[0];
															}
															//$log.debug('CASE 1 :' + indexValues1.join(',') + ' : ' + indexValues2.join(',') + ' : ' + index);
															columns = getColumns(indexValues1);
															$scope.columns.splice(indexValues1[0], indexValues1.length); //Remove n Columns
															$scope.columns.splice.apply($scope.columns, [index, 0].concat(columns)); //Add n Columns
														} else if (self.colMoveFrom.isGroupHeader && !colMoveTo.isGroupHeader) {
															indexValues1 = self.colMoveFrom.headerFieldIndices;
															index = self.findColumnIndex($scope.columns, colMoveTo.index);
															if (index > indexValues1[0]) {
																index = index - indexValues1.length + 1;
															} else {
																index = index + 1;
															}
															//$log.debug('CASE 2 :' + indexValues1.join(',') + ' : ' + index);
															columns = getColumns(indexValues1);
															$scope.columns.splice(indexValues1[0], indexValues1.length); //Remove n Columns
															$scope.columns.splice.apply($scope.columns, [index, 0].concat(columns)); //Add n Columns
														} else if (!self.colMoveFrom.isGroupHeader && colMoveTo.isGroupHeader) {
															index = self.findColumnIndex($scope.columns, self.colMoveFrom.index);
															indexValues2 = colMoveTo.headerFieldIndices;
															var startIndex = indexValues2[0] + indexValues2.length;
															if (startIndex > index) {
																startIndex = startIndex - 1;
															}
															//$log.debug('CASE 3 :' + index + ' : ' + startIndex);
															column = $scope.columns[index];
															$scope.columns.splice(index, 1); //Remove n Columns
															$scope.columns.splice(startIndex, 0, column); //Add n Columns
														}
													} else {
														if ((angular.isDefined(self.colMoveFrom.headerGroup) || angular.isDefined(colMoveTo.headerGroup)) && self.colMoveFrom.headerGroup !== colMoveTo.headerGroup) {
															return;
														}
														var fromIndex = self.findColumnIndex($scope.columns, self.colMoveFrom.index);
														var toIndex = self.findColumnIndex($scope.columns, colMoveTo.index);
														if (fromIndex > toIndex) {
															toIndex = toIndex + 1;
														}
														//$log.debug('CASE 4 :' + fromIndex + ' : ' + toIndex);
														column = $scope.columns[fromIndex];
														$scope.columns.splice(fromIndex, 1); //Remove 1 Column
														$scope.columns.splice(toIndex, 0, column); //Add 1 Column
													}
													self.fixColumnIndexes();
													setTimeout(function() {
														self.colMoveFrom = undefined;
													}, 50);
												}
											}
										});
									}
								});
								if (self.config.enableGrouping) {
									var $gridGroupPanel = $('#' + uiGrid.gridGroupPanelId);
									var $aswGroups = $gridGroupPanel.find('.aswGroup');
									$aswGroups.bind('selectstart', function() {
										return false;
									});
									var aswGroup;
									$aswGroups.each(function(index) {
										aswGroup = $(this);
										if (!aswGroup.data('ui-draggable')) {
											aswGroup.draggable({
												addClasses: false,
												zIndex: 3,
												scroll: false,
												start: function(event, ui) {
													tops.positionTop = ui.position.top;
													var aswGroup = $(event.target).closest('.aswGroup');
													var groupScope = angular.element(aswGroup).scope();
													if (groupScope) {
														self.groupColMoveFrom = {
															index: groupScope.$index
														};
													}
												},
												drag: function(event, ui) {
													ui.position.top = tops.positionTop;
												},
												revert: function() {
													$timeout(function() {
														self.groupColMoveFrom = undefined;
													}, 50);
													return true;
												}
											}).droppable({
												addClasses: false,
												hoverClass: 'highlightDrop',
												drop: function(event, ui) {
													if (!self.groupColMoveFrom) {
														return;
													}
													var aswGroup = $(event.target).closest('.aswGroup');
													var groupScope = angular.element(aswGroup).scope();
													if (groupScope) {
														var groupColMoveTo = {
															index: groupScope.$index
														};
														if (self.groupColMoveFrom.index === groupColMoveTo.$index) {
															return;
														}
														var field = $scope.groupBy[self.groupColMoveFrom.index];
														$scope.groupBy.splice(self.groupColMoveFrom.index, 1);
														$scope.groupBy.splice(groupColMoveTo.index, 0, field);
														$scope.groupBy = jQuery.extend(true, [], $scope.groupBy);
														$scope.$emit('aswGridEvent', {
															gridId: $scope.gridId,
															eventType: 'ApplyGrouping'
														});
														setTimeout(function() {
															self.groupColMoveFrom = undefined;
														}, 50);
													}
												}
											});
										}
									});
									if (!$gridGroupPanel.data('ui-droppable')) {
										$gridGroupPanel.droppable({
											addClasses: false,
											hoverClass: 'highlightDrop',
											drop: function(event, ui) {
												if (!self.colMoveFrom) {
													return;
												}
												if (!self.colMoveFrom.groupable) {
													self.colMoveFrom = undefined;
													return;
												}
												var field = $scope.columns[self.colMoveFrom.index].field;
												$scope.addGroup(field);
												setTimeout(function() {
													self.colMoveFrom = undefined;
												}, 50);
											}
										});
									}
								}
								if (self.config.enableColumnResize) {
									$aswHeaders.each(function(index) {
										aswHeader = $(this);
										if (!aswHeader.data('ui-resizable') && !aswHeader.children().hasClass('aswGroupCell')) {
											aswHeader.resizable({
												handles: 'e',
												helper: 'ui-resizable-helper',
												grid: [10, 10],
												resize: function(event, ui) {
													ui.size.width = Math.round(ui.size.width / 5) * 5;
												},
												stop: function(event, ui) {
													var headerScope = angular.element(ui.originalElement).scope();
													//$log.debug(headerScope.col.originalIndex + ' : ' + headerScope.col.resizable + ' resize : ' + [ui.originalSize.width, ui.size.width].join(','));
													if (headerScope.col.resizable) {
														self.setCustomWidth(headerScope.col.originalIndex, ui.size.width);
														gridUtils.buildStyles($scope, self);
														self.fixedHeaderTable();
													}
												}
											});
										}
									});
								}
							}
						};
						self.setRenderRows = function() {
							var rows = [];
							angular.forEach(self.data, function(entity, index) {
								var aswRow = createASWRow(entity, index, self);
								aswRow.entity.ui.expanded = true;
								rows.push(aswRow);
							});
							$scope.renderRows = rows;
							self.grouping = false;
						};
						self.setRenderGroupedRows = function() {
							$scope.renderRows = self.data;
							self.grouping = true;
						};
						self.updateTotalChildren = function() {
							var total = 0;
							var getTotal = function(aggRow) {
								total += aggRow.entity.children.length;
								if (aggRow.entity.aggChildren.length > 0) {
									angular.forEach(aggRow.entity.aggChildren, function(rowIndex, index) {
										return getTotal(self.aswAggRowRef[rowIndex]);
									});
								}
								return total;
							};
							var aggRow;
							for (var key in self.aswAggRowRef) {
								aggRow = self.aswAggRowRef[key];
								total = 0;
								aggRow.entity.totalChildren = getTotal(aggRow);
							}
						};
						self.parseGroupData = function(groupedData) {
							if (groupedData.values) {
								angular.forEach(groupedData.values, function(entity, index) {
									var aswRow = createASWRow(entity, self.rowIndex, self);
									self.rowIndex++;
									aswRow.entity.ui.expanded = self.config.groupByExpanded; //default
									self.parentCache[self.parentCache.length - 1].entity.children.push(aswRow.rowIndex);
									if (self.config.groupByExpanded) {
										self.parentCache[self.parentCache.length - 1].entity.ui.expandedChildren = true;
									}
									self.aswRowRef[aswRow.rowIndex] = aswRow;
									self.groupedRows.push(aswRow);
								});
							} else {
								for (var prop in groupedData) {
									if (prop === GROUP_NAME || prop === GROUP_DEPTH) {
										continue;
									} else if (groupedData.hasOwnProperty(prop)) {
										var aswAggRow = createASWRow({
											'label': prop,
											'group': groupedData[GROUP_NAME],
											'depth': groupedData[GROUP_DEPTH],
											'children': [],
											'aggChildren': [],
											'totalChildren': 0
										}, 'group-' + self.rowIndex, self);
										self.rowIndex++;
										aswAggRow.isAggRow = true;
										if (aswAggRow.entity.depth > 0) {
											aswAggRow.entity.ui.expanded = self.config.groupByExpanded; // default
											var aswAggRowParent = self.parentCache[aswAggRow.entity.depth - 1];
											if (self.config.groupByExpanded) {
												aswAggRowParent.entity.ui.expandedChildren = true;
											}
											aswAggRowParent.entity.aggChildren.push(aswAggRow.rowIndex);
										}
										self.groupedRows.push(aswAggRow);
										self.parentCache[aswAggRow.entity.depth] = aswAggRow;
										self.aswAggRowRef[aswAggRow.rowIndex] = aswAggRow;
										self.parseGroupData(groupedData[prop]);
									}
								}
							}
						};
						self.groupBy = function(data, dateFormat) {
							$log.debug('groupBy');
							self.groupedRows.length = 0;
							if (gridUtils.isNullOrUndefined(data) || (data && data.length === 0)) {
								return;
							}
							var groups = $scope.groupBy;
							var groupedData = {};
							var model;
							var val;
							var ptr;
							for (var x = 0; x < data.length; x++) {
								model = data[x];
								ptr = groupedData;
								for (var y = 0; y < groups.length; y++) {
									val = model[groups[y]];
									if (val === '' || val === null) {
										val = 'null';
									} else {
										if (typeof val === 'string' && (val.match(iso8601RegEx))) {
											val = $filter('date')(val, dateFormat);
										} else {
											val = val.toString();
										}
									}
									if (!ptr[val]) {
										ptr[val] = {};
									}
									if (!ptr[GROUP_NAME]) {
										ptr[GROUP_NAME] = groups[y];
									}
									if (!ptr[GROUP_DEPTH]) {
										ptr[GROUP_DEPTH] = y;
									}
									ptr = ptr[val];
								}
								if (!ptr.values) {
									ptr.values = [];
								}
								ptr.values.push(data[x]);
							}
							$log.debug('groupedData : ' + JSON.stringify(groupedData));
							self.rowIndex = 0;
							self.groupedRows.length = 0;
							self.parentCache.length = 0;
							self.aswAggRowRef = {};
							self.aswRowRef = {};
							self.parseGroupData(groupedData);
							self.updateTotalChildren();
							$log.debug('groupedRows : ' + JSON.stringify(self.groupedRows));
						};
						self.findColumnByIndex = function(columns, index) {
							for (var i = 0; i < columns.length; i++) {
								if (columns[i].index === index) {
									return columns[i];
								}
							}
							return null;
						};
						self.findColumnIndex = function(columns, index) {
							for (var i = 0; i < columns.length; i++) {
								if (columns[i].index === index) {
									return i;
								}
							}
							return -1;
						};
						self.applySorting = function(gridCol, evt) {
							//$log.debug('applySorting');
							var renderCol = null;
							var addSortColumn = function(column) {
								$scope.sortInfo.push({
									index: column.index,
									field: column.field,
									direction: column.sortDirection,
									sortingAlgorithm: column.sortingAlgorithm
								});
							};
							if (evt && evt.ctrlKey) { //evt.shiftKey
								var column = self.findColumnByIndex($scope.sortInfo, gridCol.index);
								if (column === null) {
									if ($scope.sortInfo.length === 1) {
										renderCol = self.findColumnByIndex($scope.renderCols, $scope.sortInfo[0].index);
										renderCol.sortPriority = $scope.sortInfo.length;
									}
									addSortColumn(gridCol);
									renderCol = self.findColumnByIndex($scope.renderCols, gridCol.index);
									renderCol.sortPriority = $scope.sortInfo.length;
								} else {
									column.direction = gridCol.sortDirection;
								}
							} else {
								$scope.sortInfo.length = 0;
								var isArr = jQuery.isArray(gridCol);
								if (isArr) {
									angular.forEach(gridCol, function(col, index) {
										addSortColumn(col);
										renderCol = self.findColumnByIndex($scope.renderCols, col.index);
										renderCol.sortPriority = index + 1;
									});
								} else {
									self.clearSorting(gridCol);
									renderCol = self.findColumnByIndex($scope.renderCols, gridCol.index);
									if (renderCol) {
										renderCol.sortPriority = null;
									}
									addSortColumn(gridCol);
								}
							}
							//Sorting callback
							if ($scope.sortInfo.length > 0 && self.config.sortCallBack) {
								var lastSortCol = $scope.sortInfo[$scope.sortInfo.length - 1];
								self.config.sortCallBack(lastSortCol.field, lastSortCol.direction);
							}
							$scope.$emit('aswGridEvent', {
								gridId: $scope.gridId,
								eventType: 'ApplySorting'
							});
						};
						self.clearSorting = function(gridCol) {
							//$log.debug('clearSorting');
							angular.forEach($scope.renderCols, function(col) {
								if (gridCol.index !== col.index) {
									col.sortDirection = '';
									col.sortPriority = null;
								}
							});
						};
						self.selectRow = function(entity, state) {
							//$log.debug('selectRow : ' + state);
							var index;
							entity.ui.selected = state; // UPDATE
							if (self.config.multiSelect) {
								index = $scope.selectedItems.indexOf(entity);
								if (index === -1 && state) {
									$scope.selectedItems.push(entity); // ADD
								} else if (!state) {
									$scope.selectedItems.splice(index, 1); // REMOVE
								}
							} else {
								index = $scope.selectedItems.indexOf(entity);
								if (index === -1 && state) {
									//Clear Selection.
									angular.forEach($scope.selectedItems, function(selectedItem, index) {
										selectedItem.ui.selected = false;
									});
									$scope.selectedItems.length = 0;
									$scope.selectedItems.push(entity); // ADD
								} else if (!state) {
									$scope.selectedItems.splice(index, 1); // REMOVE
								}
							}
							self.config.afterSelectionChange($scope.selectedItems);
						};
						self.setBodyVisibility = function(flag) {
							if (self.config.enablePinning && self.config.showHeading && self.$gridBody) {
								self.$gridBody.css({
									'visibility': (flag) ? 'visible' : 'hidden'
								});
							}
						};
						options.scrollTop = function(offset) {
							if (self.$gridBody) {
								self.$gridBody.find('.asw-tbody').scrollTop(0);
							}
						};
						options.selectItem = function(index, state) {
							if (angular.isDefined(index) && index < $scope.renderRows.length) {
								self.selectRow($scope.renderRows[index].entity, state);
							}
						};
						options.clearSorting = function() {
							$scope.sortInfo.length = 0;
						};

						return self;
					};

					var createASWRow = function(entity, index, grid) {
						var self = {};
						self.entity = entity;
						self.rowIndex = index;
						if (!self.entity.ui) {
							self.entity.ui = jQuery.extend({}, defaultEntityUI);
						}
						self.alternatingRowClass = function() {
							var isEven = (self.rowIndex % 2) === 0;
							var rowClass = {
								'aswRow': true,
								'even': isEven,
								'odd': !isEven,
								'selected': (self.entity.ui.selected) ? self.entity.ui.selected : false
							};
							if (grid.config.rowClassCallBack) {
								var dynamicClass = grid.config.rowClassCallBack(self.entity);
								angular.forEach(dynamicClass, function(value, key) {
									rowClass[key] = value;
								});
							}
							return rowClass;
						};
						self.toggleSelected = function(evt) {
							if (!grid.config.enableRowSelection) {
								return false;
							}
							if (evt.target.className) {
								var ignoreClassNames = 'aswToolTip';
								var className = evt.target.className.split(' ')[0];
								if (ignoreClassNames.indexOf(className) !== -1) {
									return false;
								}
							}
							grid.selectRow(self.entity, !self.entity.ui.selected);
							if (grid.config.selectCallBack) {
								grid.config.selectCallBack(self.entity);
							}
						};
						self.getProperty = function(path) {
							return gridUtils.evalProperty(self.entity, path);
						};
						self.rowStyle = function() {
							return {
								display: (self.entity.ui.expanded) ? 'table-row' : 'none'
							};
						};
						self.toggleExpand = function() {
							if (self.isAggRow) {
								var row, aggRow;
								var toggleExpandCollapseChildren = function(currRow, flag, recursive) {
									if (currRow.isAggRow) {
										currRow.entity.ui.expandedChildren = flag;
									}
									if (currRow.entity.children.length > 0) {
										angular.forEach(currRow.entity.children, function(rowIndex, index) {
											row = grid.aswRowRef[rowIndex];
											row.entity.ui.expanded = flag;
										});
									}
									if (currRow.entity.aggChildren.length > 0) {
										angular.forEach(currRow.entity.aggChildren, function(rowIndex, index) {
											aggRow = grid.aswAggRowRef[rowIndex];
											aggRow.entity.ui.expanded = flag;
											if (recursive) {
												toggleExpandCollapseChildren(aggRow, flag, recursive);
											}
										});
									}
								};
								var expandedFlag = !self.entity.ui.expandedChildren;
								toggleExpandCollapseChildren(self, expandedFlag, !expandedFlag);
							}
						};
						self.depth = function() {
							if (self.isAggRow) {
								return new Array(self.entity.depth);
							}
							return [];
						};
						return self;
					};
					var createASWCol = function(colDef, index, grid) {
						var self = {};
						self.field = colDef.field;
						self.ui = colDef.ui;
						self.tooltip = colDef.tooltip;
						self.index = index;
						self.originalIndex = colDef.originalIndex;
						self.visible = gridUtils.isNullOrUndefined(colDef.visible) || colDef.visible;
						self.width = gridUtils.isNullOrUndefined(colDef.width) ? null : colDef.width;
						self.aswcell = colDef.cellTemplate || $templateCache.get('aswcell.html').replace(CUSTOM_FILTERS, colDef.cellFilter ? '|' + colDef.cellFilter : '');
						self.cellClass = 'col' + self.originalIndex;
						self.isAggCol = false;
						if (!self.ui) {
							self.ui = {
								expandedDetail: false
							};
						}
						if (grid.config.enablePinning) {
							self.pinned = angular.isDefined(colDef.pinned) ? colDef.pinned : false;
							self.pinnable = gridUtils.isNullOrUndefined(colDef.pinnable) || colDef.pinnable;
							self.togglePin = function(evt) {
								//$log.debug(self.pinned + ' renderPinnedCols = ' + $scope.renderPinnedCols.length + ' : renderCols = ' + $scope.renderCols.length);
								if (self.pinned && $scope.renderPinnedCols.length === 1) {
									return;
								}
								if (!self.pinned && $scope.renderCols.length === 1) {
									return;
								}

								if (self.pinnable) {
									var indexFrom = self.index;
									var indexTo = 0;
									for (var i = 0; i < $scope.columns.length; i++) {
										if ($scope.columns[i].pinnable && !$scope.columns[i].pinned) {
											break;
										}
										indexTo++;
									}
									if (self.pinned) {
										indexTo = Math.max(self.originalIndex, indexTo - 1);
									}
									self.pinned = !self.pinned;

									if (indexTo !== indexFrom) {
										$scope.columns.splice(indexFrom, 1);
										$scope.columns.splice(indexTo, 0, self);
										grid.fixColumnIndexes();
									} else {
										grid.buildRenderColumns();
									}
								}
							};
						} else {
							self.pinned = false;
							self.pinnable = false;
						}
						if (grid.config.showHeading) {
							self.displayName = gridUtils.isNullOrUndefined(colDef.displayName) ? colDef.field : colDef.displayName;
							self.headerClass = 'col' + self.originalIndex;
							self.aswheadercell = colDef.headerCellTemplate || $templateCache.get('aswheadercell.html');
							self.aswheaderdummycell = colDef.headerDummyCellTemplate || $templateCache.get('aswheaderdummycell.html');
							self.isGroupHeader = false;
							self.showHideGroupDetail = false;
							self.headerGroup = colDef.headerGroup;
							if (angular.isDefined(self.headerGroup)) {
								var headerGroupDef = grid.config.headerGroupDefs[self.headerGroup];
								if (angular.isDefined(headerGroupDef)) {
									if (angular.isDefined(headerGroupDef.showHideDetail)) {
										self.showHideGroupDetail = headerGroupDef.showHideDetail;
									}
									self.aswgroupheadercell = headerGroupDef.headerGroupTemplate || $templateCache.get('aswheadergroupcell.html');
								} else {
									self.aswgroupheadercell = $templateCache.get('aswheadergroupcell.html');
								}
							}
							if (grid.config.enableFiltering) {
								self.headerFilter = jQuery.extend({}, defaultHeaderFilter, colDef.headerFilter);
								if (!grid.config.enableFiltering || angular.isUndefined(self.field)) {
									self.headerFilter.enableFiltering = false;
								}
							}
						}
						if (grid.config.enableSorting) {
							self.sortingAlgorithm = colDef.sortFn;
							self.sortDirection = '';
							self.sortable = gridUtils.isNullOrUndefined(colDef.sortable) || colDef.sortable;
							if (self.sortable && angular.isUndefined(self.field)) {
								self.sortable = false;
							}
							self.sort = function(evt) {
								if (!self.sortable || grid.colMoveFrom || grid.groupColMoveFrom) {
									return true;
								}
								self.sortDirection = (self.sortDirection === ASC) ? DESC : ASC;
								grid.applySorting(self, evt);
								return false;
							};
							self.sortImg = function() {
								if (self.sortable) {
									if (self.sortDirection === '') {
										return 'sort_both.png';
									} else if (self.sortDirection === ASC) {
										return 'sort_asc.png';
									} else if (self.sortDirection === DESC) {
										return 'sort_desc.png';
									}
								}
								return 'sort_both.png';
							};

						} else {
							self.sortable = false;
						}
						if (grid.config.enableGrouping) {
							self.groupable = gridUtils.isNullOrUndefined(colDef.groupable) || colDef.groupable;
							if (self.groupable && angular.isUndefined(self.field)) {
								self.groupable = false;
							}
							self.removeGroup = function(index) {
								if (grid.groupColMoveFrom) {
									return true;
								}
								$scope.groupBy.splice(index, 1);
								$scope.$emit('aswGridEvent', {
									gridId: $scope.gridId,
									eventType: 'ApplyGrouping'
								});
								return false;
							};
						} else {
							self.groupable = false;
						}
						if (grid.config.enableColumnResize) {
							self.resizable = gridUtils.isNullOrUndefined(colDef.resizable) || colDef.resizable;
						} else {
							self.resizable = false;
						}
						if (colDef.headerClass) {
							self.headerClass += ' ' + colDef.headerClass;
						}
						if (colDef.cellClass) {
							self.cellClass += ' ' + colDef.cellClass;
							if (!colDef.headerClass) {
								self.headerClass += ' ' + colDef.cellClass;
							}
						}
						if (self.groupable) {
							self.headerClass += ' aswGroupable';
						}
						self.cursor = self.sortable ? 'pointer' : 'default';

						return self;
					};
					var createASWHeaderGroup = function(renderCol, headerDetail, grid) {
						var self = {};
						var headerGroup = renderCol.headerGroup;
						self.index = 333 + renderCol.index;
						self.originalIndex = renderCol.originalIndex;
						self.groupable = renderCol.groupable;
						self.displayName = headerGroup;
						self.headerGroup = headerGroup;
						self.isAggCol = false;
						self.isGroupHeader = true;
						self.isShowHideGroupDetail = renderCol.showHideGroupDetail;
						self.aswgroupheadercell = renderCol.aswgroupheadercell;
						self.headerClass = 'aswHeaderGroup';
						self.rowSpan = 1;
						self.colSpan = headerDetail.colSpan;
						self.headerVisibleFieldIndices = headerDetail.visibleFieldIndices;
						self.headerFieldIndices = headerDetail.fieldIndices;
						self.ui = renderCol.ui;
						self.showHideGroupDetail = function() {
							if (self.isShowHideGroupDetail) {
								var column = grid.findColumn(self.originalIndex);
								column.ui.expandedDetail = !column.ui.expandedDetail;
								var visibility = column.ui.expandedDetail;
								for (var i = 1; i < self.headerFieldIndices.length; i++) {
									$scope.columns[self.headerFieldIndices[i]].visible = visibility;
								}
								$scope.$emit('aswGridEvent', {
									gridId: $scope.gridId,
									eventType: 'ApplySetting'
								});
							}
						};
						return self;
					};
					var createASWGroupCell = function(index) {
						var self = {};
						self.headerClass = 'aswGroupCell';
						self.cellClass = 'aswGroupCell';
						self.index = 666 + index;
						self.isAggCol = true;
						self.isGroupHeader = false;
						self.filter = {};
						return self;
					};

					//Actions
					$scope.pageToFirst = function() {
						$scope.pagingOptions.currentPage = 1;
					};
					$scope.pageBackward = function() {
						var page = $scope.pagingOptions.currentPage;
						$scope.pagingOptions.currentPage = Math.max(page - 1, 1);
					};
					$scope.pageForward = function() {
						var curPage = $scope.pagingOptions.currentPage;
						$scope.pagingOptions.currentPage = Math.min(curPage + 1, $scope.pagingOptions.maxPages);
					};
					$scope.pageToLast = function() {
						$scope.pagingOptions.currentPage = $scope.pagingOptions.maxPages;
					};
					$scope.cantPageBackward = function() {
						var curPage = $scope.pagingOptions.currentPage;
						return curPage <= 1;
					};
					$scope.cantPageForward = function() {
						var curPage = $scope.pagingOptions.currentPage;
						var maxPages = $scope.pagingOptions.maxPages;
						return curPage >= maxPages;
					};
					$scope.isSelectAllRows = function() {
						var renderRows = $scope.renderRows;
						for (var i = 0; i < renderRows.length; i++) {
							if (!renderRows[i].entity.ui.selected) {
								return false;
							}
						}
						return true;
					};
					$scope.toggleSelectAllRows = function($event) {
						var renderRows = $scope.renderRows;
						for (var i = 0; i < renderRows.length; i++) {
							uiGrid.selectRow(renderRows[i].entity, $event.target.checked);
						}
					};
					$scope.launchSelectAll = function($event, col) {
						var isOpen = angular.isUndefined(col.isOpen) ? false : col.isOpen;
						var element = $($event.target);
						while (element[0].className.indexOf('aswHeaderText') === -1) {
							element = element.parent();
						}
						var headerFilter = element.find('.aswHeaderFilter');
						if (angular.isDefined(headerFilter)) {
							var aswHeader = gridUtils.getASWHeader(headerFilter);
							if (!isOpen) {
								gridUtils.hideAllHeaderFilter($scope);
								var dropdownMenu = $('#' + $scope.gridId + ' .aswHeaderFilter .dropdown-menu');
								dropdownMenu.css({
									'top': aswHeader.outerHeight()
								});
								if (aswHeader.data('ui-draggable')) {
									aswHeader.draggable('disable');
								}
								col.isOpen = true;
							} else {
								if (aswHeader.data('ui-draggable')) {
									aswHeader.draggable('enable');
								}
								col.isOpen = false;
							}
						}
					};
					$scope.closeSelectAll = function($event, col) {
						var element = $($event.target);
						var aswHeaderFilter = element.parent().parent();
						if (angular.isDefined(aswHeaderFilter)) {
							$animate.removeClass(aswHeaderFilter, 'open');
							col.isOpen = false;
						}
					};
					$scope.selectAllPages = function($event, col, selectAllFlag) {
						var dataConfigName = uiGrid.config.data;
						var dataCopy = dataConfigName + 'Copy';
						var data = $scope.$parent[dataCopy];
						for (var i = 0; i < data.length; i++) {
							if (!data[i].ui) {
								data[i].ui = jQuery.extend({}, defaultEntityUI);
							}
							uiGrid.selectRow(data[i], selectAllFlag);
						}
						$scope.closeSelectAll($event, col);
					};
					$scope.addGroup = function(field) {
						if (angular.isDefined(field)) {
							$scope.groupBy.push(field);
							$scope.$emit('aswGridEvent', {
								gridId: $scope.gridId,
								eventType: 'ApplyGrouping'
							});
						}
					};

					$scope.syncTableWidthHeight = function() {
						uiGrid.syncTableWidthHeight();
					};

					//Initialize
					var uiGridConfig = $scope.$eval($attrs.aswGrid);
					var uiGrid = createASWGrid(uiGridConfig);
					uiGrid.initialize();
					var gridTpl = $templateCache.get('aswgrid.html');
					gridTpl = gridTpl.replace(ASW_GRID_BODY, uiGrid.gridBodyId);
					gridTpl = gridTpl.replace(ASW_GRID_GROUPPANEL, uiGrid.gridGroupPanelId);

					$element.append($compile(gridTpl)($scope));
					$element.addClass(uiGrid.gridClassId);

					$scope.$on('aswGridEvent', function(event, eventInfo) {
						if ($scope.gridId !== eventInfo.gridId) {
							return;
						}
						$log.debug(uiGrid.config.data + ' : aswGridEvent : ' + JSON.stringify(eventInfo));
						var data;
						var dataConfigName = uiGrid.config.data;
						var pagingOptions;
						var groups;
						var dataCopy = dataConfigName + 'Copy';
						var dataFilter = dataConfigName + 'Filter';
						switch (eventInfo.eventType) {
							case 'OnReady':
								uiGrid.$gridBody = $('#' + uiGrid.gridBodyId);
								uiGrid.setBodyVisibility(false);
								gridUtils.buildStyles($scope, uiGrid);
								if (uiGrid.config.showHeading) {
									uiGrid.scheduleEvent('setJQueryUIWidgets', 300, function() {
										uiGrid.setJQueryUIWidgets();
									});
									uiGrid.scheduleEvent('fixedHeaderTable', 300, function() {
										uiGrid.fixedHeaderTable();
									});
								}
								break;
							case 'ResetFiltering':
								if ($scope.enableFiltering) {
									gridUtils.hideAllHeaderFilter($scope);
									angular.forEach($scope.renderCols, function(renderCol, index) {
										renderCol.isOpen = false;
										renderCol.headerFilter.filtered = false;
										renderCol.headerFilter.value = undefined;
										renderCol.headerFilter.operator = undefined;
										renderCol.headerFilter.selectedValues = [];
									});
									var filterOptions = $scope.filterOptions;
									var criteriaRef = {};
									angular.forEach(filterOptions.criteria, function(colCriteria, index) {
										colCriteria.operator = '=';
										colCriteria.value = '';
										criteriaRef[colCriteria.property] = colCriteria;
										colCriteria.values = [];
									});
									filterOptions.wildcard = eventInfo.filterOptions.wildcard;
									angular.forEach(eventInfo.filterOptions.criteria, function(colCriteria, index) {
										criteriaRef[colCriteria.property].operator = colCriteria.operator;
										criteriaRef[colCriteria.property].value = colCriteria.value;
										criteriaRef[colCriteria.property].values = colCriteria.values;
									});
									$scope.$parent[dataConfigName + 'FilterOptions'] = filterOptions;
								}
								break;
							case 'ApplyFiltering':
								if ($scope.enableFiltering) {
									data = $scope.$parent[dataCopy];
									$scope.$parent[dataFilter] = $filter('aswGridFilter')(data, $scope.filterOptions.wildcard, $scope.filterOptions.criteria, $scope.calendar.dateFormat);
								}
								$scope.$emit('aswGridEvent', {
									gridId: eventInfo.gridId,
									eventType: 'ApplySorting'
								});
								break;
							case 'ApplySorting':
								if ($scope.sortInfo.length > 0 && uiGrid.config.sorted === false) {
									data = $scope.enableFiltering ? $scope.$parent[dataFilter] : $scope.$parent[dataCopy];
									gridSortUtils.Sort(uiGrid.config, $scope.sortInfo, data);
									if (typeof uiGrid.config.data === 'string' && uiGrid.config.sortedData) {
										uiGrid.config.sortedData(data);
									}
									if ($scope.enableFiltering) {
										$scope.$parent[dataFilter] = data;
									} else {
										$scope.$parent[dataCopy] = data;
									}
								}
								$scope.$emit('aswGridEvent', {
									gridId: eventInfo.gridId,
									eventType: 'ApplyGrouping'
								});
								break;
							case 'ApplyGrouping':
								pagingOptions = dataConfigName + 'PagingOptions';
								groups = $scope.groupBy;
								if (!$scope.enableGrouping || gridUtils.isNullOrUndefined(groups) || (groups && groups.length === 0)) {
									if ($scope.enableFiltering) {
										var filteredData = {
											data: $scope.$parent[dataFilter],
											filtering: true
										};
										aswPagingOptions.setPagingData($scope.$parent, dataConfigName, filteredData, $scope.$parent[pagingOptions].currentPage, $scope.$parent[pagingOptions].pageSize);
									} else {
										$scope.$parent[pagingOptions].currentPage = 1;
										aswPagingOptions.setPagingData($scope.$parent, dataConfigName, $scope.$parent[dataCopy], $scope.$parent[pagingOptions].currentPage, $scope.$parent[pagingOptions].pageSize);
									}
								} else {
									data = $scope.enableFiltering ? $scope.$parent[dataFilter] : $scope.$parent[dataCopy];
									uiGrid.groupBy(data, $scope.calendar.dateFormat);
									$scope.$parent[pagingOptions].currentPage = 1;
									var groupedData = {
										data: uiGrid.groupedRows,
										grouping: true,
										totalAggRows: (Object.keys(uiGrid.aswAggRowRef)).length
									};
									aswPagingOptions.setPagingData($scope.$parent, dataConfigName, groupedData, $scope.$parent[pagingOptions].currentPage, $scope.$parent[pagingOptions].pageSize);
								}
								$scope.$emit('aswGridEvent', {
									gridId: eventInfo.gridId,
									eventType: 'ApplySetting'
								});
								break;
							case 'RenderRows':
								uiGrid.setBodyVisibility(false);
								groups = $scope.groupBy;
								if (gridUtils.isNullOrUndefined(groups) || (groups && groups.length === 0)) {
									uiGrid.setRenderRows();
								} else {
									uiGrid.setRenderGroupedRows();
								}
								if (uiGrid.config.showHeading) {
									uiGrid.scheduleEvent('fixedHeaderTable', 300, function() {
										uiGrid.fixedHeaderTable();
									});
								}
								break;
							case 'ApplySetting':
								uiGrid.buildRenderColumns();
								if (uiGrid.config.showHeading) {
									uiGrid.$gridBody.find('.asw-tbody').scrollTop(0);
									if (uiGrid.config.enableColumnResize) {
										gridUtils.buildStyles($scope, uiGrid);
									}
									if (uiGrid.config.showHeading) {
										uiGrid.scheduleEvent('fixedHeaderTable', 300, function() {
											uiGrid.fixedHeaderTable();
										});
									}
								}
								break;
						}
					});
					//DomReady
					$timeout(function() {
						$attrs.$set('id', uiGrid.gridId);
						uiGridConfig.$gridScope = $scope;
						$scope.$emit('aswGridEvent', {
							gridId: uiGrid.gridId,
							eventType: 'OnReady'
						});
					});
					//Destroy
					$scope.$on('$destroy', function cleanOptions() {
						//$log.debug('aswGrid : cleanOptions');
					});
				}
			};
		}
	]);

	angular.module('cargocommonApp').directive('aswGridRenderBody', ['$compile', '$templateCache', '$log',
		function($compile, $templateCache, $log) {
			return {
				restrict: 'E',
				link: function($scope, $element, $attrs) {
					//$log.debug('aswRenderGridBody : ' + $attrs.type);
					var bodyTpl = $templateCache.get('aswgridbody.html');
					switch ($attrs.type) {
						case 'PINNED-COLUMN':
							/*
								Replace : RENDER_HEADER_ROWS 	: renderPinnedHeaderRows
								Replace : ASW_ROW_TYPE 			: PINNED
							*/
							bodyTpl = bodyTpl.replace(RENDER_HEADER_ROWS, 'renderPinnedHeaderRows');
							bodyTpl = bodyTpl.replace(ASW_ROW_TYPE, 'PINNED');
							bodyTpl = bodyTpl.replace(NO_RECORD_FOUND_MSG, '');
							break;
						case 'PINNED-BODY':
						case 'DEFAULT':
							/*
								Replace : RENDER_HEADER_ROWS 	: renderHeaderRows
								Replace : ASW_ROW_TYPE 			: DEFAULT
							*/
							bodyTpl = bodyTpl.replace(RENDER_HEADER_ROWS, 'renderHeaderRows');
							bodyTpl = bodyTpl.replace(ASW_ROW_TYPE, 'DEFAULT');
							bodyTpl = bodyTpl.replace(NO_RECORD_FOUND_MSG, '<tr ng-if="showNoRecordFoundMsg && dataInitialized && renderRows.length == 0"><td colspan="50"><div asw-no-record-found="{{noRecordFoundMsg}}"/></td></tr>');
					}
					$element.html($compile(bodyTpl)($scope));
				}
			};
		}
	]);

	angular.module('cargocommonApp').directive('aswRow', ['$compile', '$templateCache',
		function($compile, $templateCache) {
			return {
				scope: false,
				compile: function() {
					return {
						pre: function($scope, $element, $attrs) {
							var aswrowTpl = $templateCache.get('aswrowtemplate.html');
							var aswaggrowTpl = $templateCache.get('aswaggrowtemplate.html');
							switch ($attrs.aswRow) {
								case 'PINNED':
									/*
										Replace : RENDER_COLS 	: renderPinnedCols
									*/
									aswrowTpl = aswrowTpl.replace(RENDER_COLS, 'renderPinnedCols');
									aswaggrowTpl = '<td colspan="50"></td>';
									break;
								case 'DEFAULT':
									/*
										Replace : RENDER_COLS 	: renderCols
									*/
									aswrowTpl = aswrowTpl.replace(RENDER_COLS, 'renderCols');
									aswaggrowTpl = aswaggrowTpl.replace(RENDER_COLS, 'renderCols');
							}
							if ($scope.row.isAggRow) {
								$element.append($compile(aswaggrowTpl)($scope));
							} else {
								$element.append($compile(aswrowTpl)($scope));
							}
						},
						post: function($scope, $element, $attrs) {
							var rowDetailTpl;
							switch ($attrs.aswRow) {
								case 'PINNED':
									if (!$scope.row.isAggRow) {
										rowDetailTpl = '<tr class="asw-row aswRow aswRowDetail" ng-if="row.entity.ui.expandedDetail"><td colspan="50"></td></tr>';
										$element.after($compile(rowDetailTpl)($scope));
									}
									break;
								case 'DEFAULT':
									if (!$scope.row.isAggRow) {
										rowDetailTpl = $templateCache.get($scope.gridId + 'rowDetailTemplate.html');
										if (angular.isDefined(rowDetailTpl)) {
											$element.after($compile(rowDetailTpl)($scope));
										}
									}
							}
						}
					};
				}
			};
		}
	]);

	angular.module('cargocommonApp').directive('aswHeaderCell', ['$compile', '$log',
		function($compile, $log) {
			return {
				scope: false,
				compile: function() {
					return {
						pre: function($scope, $element, $attrs) {
							switch ($attrs.aswHeaderCell) {
								case 'DUMMY':
									if (!$scope.col.isAggCol && !$scope.col.isGroupHeader) {
										var headerTpl = $scope.col.aswheaderdummycell;
										$element.append($compile(headerTpl)($scope));
										if ($scope.enableColumnResize) {
											var resizableTpl = '<div class="resizablehidden"/>';
											$element.parent().append($compile(resizableTpl)($scope));
										}
									}
									if ($scope.col.isGroupHeader) {
										$element.append($compile($scope.col.aswgroupheadercell)($scope));
									}
									break;
								case 'DEFAULT':
									if (!$scope.col.isAggCol && !$scope.col.isGroupHeader) {
										$element.append($compile($scope.col.aswheadercell)($scope));
									}
									if ($scope.col.isGroupHeader) {
										$element.append($compile($scope.col.aswgroupheadercell)($scope));
									}
							}
						}
					};
				}
			};
		}
	]);

	angular.module('cargocommonApp').directive('aswCell', ['$compile', 'gridUtils',
		function($compile, gridUtils) {
			return {
				scope: false,
				compile: function() {
					return {
						pre: function($scope, $element) {
							if (!$scope.col.isAggCol) {
								var cellTemplate = $scope.col.aswcell.replace(COL_FIELD, gridUtils.preEval('row.entity.' + $scope.col.field));
								if ($scope.col.tooltip) {
									var tooltipValue = 'asw-tool-tip="{{' + gridUtils.preEval('row.entity.' + $scope.col.tooltip) + '}}"';
									cellTemplate = cellTemplate.replace(COL_TOOLTIP, tooltipValue);
								} else {
									cellTemplate = cellTemplate.replace(COL_TOOLTIP, '');
								}
								$element.append($compile(cellTemplate)($scope));
							}
						}
					};
				}
			};
		}
	]);

	angular.module('cargocommonApp').directive('aswHeaderFilter', ['$compile', '$log', '$templateCache', '$animate', '$timeout', 'gridUtils',
		function($compile, $log, $templateCache, $animate, $timeout, gridUtils) {
			return {
				scope: false,
				controller: function($scope, $element, $attrs) {
					var element = $($element);
					var createHeaderFilter = function() {
						var self = {};
						self.initialized = false;
						self.initialize = function() {
							self.initialized = true;
						};
						self.hideHeaderFilter = function() {
							var headerFilter = element.find('.aswHeaderFilter');
							if (angular.isDefined(headerFilter)) {
								var aswHeader = gridUtils.getASWHeader(headerFilter);
								if (aswHeader.data('ui-draggable')) {
									aswHeader.draggable('enable');
								}
								$scope.col.isOpen = false;
							}
						};
						self.showHeaderFilter = function() {
							gridUtils.hideAllHeaderFilter($scope);
							var headerFilter = element.find('.aswHeaderFilter');
							if (angular.isDefined(headerFilter)) {
								var aswHeader = gridUtils.getASWHeader(headerFilter);
								if (aswHeader.data('ui-draggable')) {
									aswHeader.draggable('disable');
								}
								var dropdownMenu = $('#' + $scope.gridId + ' .aswHeaderFilter .dropdown-menu');
								dropdownMenu.css({
									'top': aswHeader.outerHeight()
								});
								$scope.col.isOpen = true;
							}
						};
						self.launchHeaderFilter = function() {
							var isOpen = angular.isUndefined($scope.col.isOpen) ? false : $scope.col.isOpen;
							if (!isOpen) {
								if ($scope.col.headerFilter.valueType === 'SingleValue') {
									if (angular.isUndefined($scope.col.headerFilter.operator)) {
										$scope.col.headerFilter.operator = $scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].operator;
									}
									if (angular.isUndefined($scope.col.headerFilter.value)) {
										$scope.col.headerFilter.value = $scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].value;
									}
								} else if ($scope.col.headerFilter.valueType === 'CustomValues') {
									if (angular.isUndefined($scope.col.headerFilter.value)) {
										$scope.col.headerFilter.value = $scope.col.headerFilter.defaultValueIndex;
									}
								} else if ($scope.col.headerFilter.valueType === 'MultiValues') {
									if (angular.isUndefined($scope.col.headerFilter.selectedValues)) {
										$scope.col.headerFilter.selectedValues = [];
									}
								}
								self.showHeaderFilter();
							} else {
								self.hideHeaderFilter();
							}
						};
						self.isSelectedAll = function() {
							if ($scope.col.headerFilter.selectedValues && $scope.col.headerFilter.values) {
								return $scope.col.headerFilter.selectedValues.length === $scope.col.headerFilter.values.length;
							}
							return false;
						};
						self.selectAll = function(checked) {
							if (checked) {
								$scope.col.headerFilter.selectedValues = $scope.col.headerFilter.values.slice(0);
							} else {
								$scope.col.headerFilter.selectedValues = [];
							}
						};
						self.isSelected = function(value) {
							if ($scope.col.headerFilter.selectedValues) {
								return $scope.col.headerFilter.selectedValues.indexOf(value) !== -1;
							}
							return false;
						};
						self.updateSelection = function(value, checked) {
							var valueIndex = $scope.col.headerFilter.selectedValues.indexOf(value);
							if (checked && valueIndex === -1) {
								$scope.col.headerFilter.selectedValues.push(value);
							}
							if (!checked && valueIndex !== -1) {
								$scope.col.headerFilter.selectedValues.splice(valueIndex, 1);
							}
						};
						self.applyHeaderFilter = function() {
							self.hideHeaderFilter();
							if ($scope.col.headerFilter.valueType === 'SingleValue') {
								$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].operator = $scope.col.headerFilter.operator;
								$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].value = $scope.col.headerFilter.value;
								$scope.col.headerFilter.filtered = ($scope.col.headerFilter.value !== '');
							} else if ($scope.col.headerFilter.valueType === 'CustomValues') {
								$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].operator = $scope.col.headerFilter.values[$scope.col.headerFilter.value].operator;
								$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].value = $scope.col.headerFilter.values[$scope.col.headerFilter.value].value;
								$scope.col.headerFilter.filtered = ($scope.col.headerFilter.value !== '');
							} else if ($scope.col.headerFilter.valueType === 'MultiValues') {
								if ($scope.col.headerFilter.selectedValues.length < $scope.col.headerFilter.values.length) {
									$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].values = $scope.col.headerFilter.selectedValues.slice(0);
								} else {
									$scope.filterOptions.criteria[$scope.col.headerFilter.criteriaIndex].values = [];
								}
								$scope.col.headerFilter.filtered = ($scope.col.headerFilter.selectedValues.length > 0);
							}
						};
						self.clearHeaderFilter = function() {
							if ($scope.col.headerFilter.valueType === 'SingleValue') {
								$scope.col.headerFilter.operator = '=';
								$scope.col.headerFilter.value = '';
							} else if ($scope.col.headerFilter.valueType === 'CustomValues') {
								$scope.col.headerFilter.value = $scope.col.headerFilter.defaultValueIndex;
							} else if ($scope.col.headerFilter.valueType === 'MultiValues') {
								$scope.col.headerFilter.selectedValues = [];
							}
							self.applyHeaderFilter();
						};
						return self;
					};
					var uiHeaderFilter = createHeaderFilter();
					$scope.launchHeaderFilter = function(evt) {
						if (!uiHeaderFilter.initialized) {
							uiHeaderFilter.initialize();
						}
						uiHeaderFilter.launchHeaderFilter();
					};
					$scope.isSelectedAll = function() {
						return uiHeaderFilter.isSelectedAll();
					};
					$scope.selectAll = function($event) {
						uiHeaderFilter.selectAll($event.target.checked);
					};
					$scope.isSelected = function(value) {
						return uiHeaderFilter.isSelected(value);
					};
					$scope.updateSelection = function(value, $event) {
						uiHeaderFilter.updateSelection(value, $event.target.checked);
					};
					$scope.applyFilter = function() {
						uiHeaderFilter.applyHeaderFilter();
					};
					$scope.clearFilter = function() {
						uiHeaderFilter.clearHeaderFilter();
					};
					$scope.closeFilter = function() {
						uiHeaderFilter.hideHeaderFilter();
					};
				},
				link: function($scope, $element, $attrs) {
					var aswFilterTpl = $compile($templateCache.get('aswheaderfilter.html'))($scope);
					$element.append(aswFilterTpl);
				}
			};
		}
	]);

	angular.module('cargocommonApp').factory('gridUtils', ['$log', '$parse', '$window', '$timeout', '$animate',
		function($log, $parse, $window, $timeout, $animate) {
			var self = {};
			self.preEval = function preEval(path) {
				var m = BRACKET_REGEXP.exec(path);
				if (m) {
					return (m[1] ? preEval(m[1]) : m[1]) + m[2] + (m[3] ? preEval(m[3]) : m[3]);
				} else {
					path = path.replace(APOS_REGEXP, '\\\'');
					var parts = path.split(DOT_REGEXP);
					var preparsed = [parts.shift()];
					angular.forEach(parts, function(part) {
						preparsed.push(part.replace(FUNC_REGEXP, '\']$1'));
					});
					return preparsed.join('[\'');
				}
			};
			self.newId = function() {
				var id = new Date().getTime();
				return id += 1;
			};
			self.isNullOrUndefined = function(obj) {
				if (obj === undefined || obj === null) {
					return true;
				}
				return false;
			};
			self.evalProperty = function(entity, path) {
				return $parse(self.preEval('entity.' + path))({
					entity: entity
				});
			};
			self.setStyleText = function(grid, css) {
				//$log.debug('setStyleText : ' + css);
				var gridId = grid.gridId,
					style = grid.styleSheet,
					styleId = gridId + '_style',
					doc = $window.document;
				if (!style) {
					style = doc.getElementById(styleId);
				}
				if (!style) {
					style = doc.createElement('style');
					style.type = 'text/css';
					style.id = styleId;
					(doc.head || doc.getElementsByTagName('head')[0]).appendChild(style);
				}
				if (style.styleSheet && !style.sheet) {
					style.styleSheet.cssText = css;
				} else {
					style.innerHTML = css;
				}
				grid.styleSheet = style;
				grid.styleText = css;
			};
			self.buildStyles = function($scope, grid) {
				//$log.debug('buildStyles');
				var gridId = grid.gridId,
					gridBodyId = grid.gridBodyId,
					renderCols = jQuery.merge(jQuery.merge([], $scope.renderPinnedCols), $scope.renderCols),
					css,
					height,
					width;
				css = '#' + gridBodyId + '{';
				if (angular.isDefined(grid.config.maxHeight)) {
					css += 'overflow-y:auto;';
					if (grid.config.maxHeight.toString().indexOf('%') === -1) {
						height = grid.config.maxHeight + 'px;';
					} else {
						height = grid.config.maxHeight;
					}
					css += 'max-height:' + height;
				}
				if (angular.isDefined(grid.config.maxWidth)) {
					css += 'overflow-x:auto;';
					if (grid.config.maxWidth.toString() === 'auto') {
						width = $(window).width() + 'px;';
					} else if (grid.config.maxWidth.toString().indexOf('%') === -1) {
						width = grid.config.maxWidth + 'px;';
					} else {
						width = grid.config.maxWidth;
					}
					css += 'max-width:' + width;
				}
				css += '}';
				css += '#' + gridBodyId + ' .asw-tbody {';
				if (angular.isDefined(grid.config.minWidth)) {
					css += 'min-width:' + grid.config.minWidth + 'px;';
				}
				if (angular.isDefined(grid.config.minHeight)) {
					css += 'min-height:' + grid.config.minHeight + 'px;';
				}
				css += '}';
				var colCSS = '#' + gridBodyId + ' .col';
				angular.forEach(renderCols, function(renderCol, index) {
					if (renderCol.originalIndex) {
						width = null;
						if (renderCol.width !== null) {
							width = renderCol.width;
						}
						if (renderCol.customWidth) {
							width = renderCol.customWidth;
						}
						if (width !== null) {
							/*
							css += colCSS + renderCol.originalIndex + '{';
							css += 'position: relative;';
							css += '}';
							*/
							css += colCSS + renderCol.originalIndex + '.aswHeaderCell, ';
							css += colCSS + renderCol.originalIndex + ' .aswCellText';
							css += '{';
							if (width.toString().indexOf('%') === -1) {
								css += 'width: ' + width + 'px;overflow: hidden;text-overflow: ellipsis;';
							} else {
								css += 'width: ' + width + ';';
							}
							css += '}';
						}
					}
				});
				self.setStyleText(grid, css);
			};
			self.replaceAll = function(str, find, replace) {
				return str.replace(new RegExp(find, 'g'), replace);
			};
			self.getASWHeader = function(currElement) {
				var aswHeader = currElement;
				while (aswHeader[0].className.indexOf('aswHeader ') === -1) {
					aswHeader = aswHeader.parent();
				}
				return aswHeader;
			};
			self.hideAllHeaderFilter = function($scope) {
				angular.forEach($scope.renderCols, function(renderCol, index) {
					renderCol.isOpen = false;
				});
				angular.forEach($scope.renderPinnedCols, function(renderCol, index) {
					renderCol.isOpen = false;
				});
			};
			return self;
		}
	]);
	angular.module('cargocommonApp').factory('gridSortUtils', ['$log', '$parse', '$timeout', 'gridUtils',
		function($log, $parse, $timeout, gridUtils) {
			var self = {};
			self.colSortFnCache = {};
			self.isCustomSort = false;

			self.guessSortFn = function(item) {
				var itemType = typeof(item);
				switch (itemType) {
					case 'number':
						return self.sortNumber;
					case 'boolean':
						return self.sortBool;
					case 'string':
						return item.match(/^[-+]?[Ã‚Â£$Ã‚Â¤]?[\d,.]+%?$/) ? self.sortNumberStr : self.sortAlpha;
					default:
						if (Object.prototype.toString.call(item) === '[object Date]') {
							return self.sortDate;
						} else {
							return self.basicSort;
						}
				}
			};
			self.basicSort = function(a, b) {
				if (a === b) {
					return 0;
				}
				if (a < b) {
					return -1;
				}
				return 1;
			};
			self.sortNumber = function(a, b) {
				return a - b;
			};
			self.sortNumberStr = function(a, b) {
				var numA, numB, badA = false,
					badB = false;
				numA = parseFloat(a.replace(/[^0-9.-]/g, ''));
				if (isNaN(numA)) {
					badA = true;
				}
				numB = parseFloat(b.replace(/[^0-9.-]/g, ''));
				if (isNaN(numB)) {
					badB = true;
				}
				if (badA && badB) {
					return 0;
				}
				if (badA) {
					return 1;
				}
				if (badB) {
					return -1;
				}
				return numA - numB;
			};
			self.sortAlpha = function(a, b) {
				if (angular.isUndefined(b) && angular.isUndefined(a)) {
					return 0;
				} else if (angular.isUndefined(b)) {
					return 1;
				} else if (angular.isUndefined(a)) {
					return -1;
				}
				var strA = a.toLowerCase(),
					strB = b.toLowerCase();
				return strA === strB ? 0 : (strA < strB ? -1 : 1);
			};
			self.sortDate = function(a, b) {
				if (angular.isUndefined(b) && angular.isUndefined(a)) {
					return 0;
				} else if (angular.isUndefined(b)) {
					return 1;
				} else if (angular.isUndefined(a)) {
					return -1;
				}
				var timeA = a.getTime(),
					timeB = b.getTime();
				return timeA === timeB ? 0 : (timeA < timeB ? -1 : 1);
			};
			self.sortBool = function(a, b) {
				if (a && b) {
					return 0;
				}
				if (!a && !b) {
					return 0;
				} else {
					return a ? 1 : -1;
				}
			};
			self.getSortFn = function(col, data) {
				var sortFn, item;
				if (self.colSortFnCache[col.field]) {
					sortFn = self.colSortFnCache[col.field];
				} else if (col.sortingAlgorithm !== undefined) {
					sortFn = col.sortingAlgorithm;
					self.colSortFnCache[col.field] = col.sortingAlgorithm;
					self.isCustomSort = true;
				} else {
					item = data[0];
					if (!item) {
						return sortFn;
					}
					sortFn = self.guessSortFn($parse('entity[\'' + col.field.replace(DOT_REGEXP, '\'][\'') + '\']')({
						entity: item
					}));
					if (sortFn) {
						self.colSortFnCache[col.field] = sortFn;
					} else {
						sortFn = self.sortAlpha;
					}
				}
				return sortFn;
			};
			self.sortData = function(sortInfo, data) {
				if (!data || !sortInfo) {
					return;
				}
				var l = sortInfo.length,
					col,
					d = data.slice(0);
				data.sort(function(itemA, itemB) {
					var tem = 0,
						idx = 0,
						res,
						sortFn;
					while (tem === 0 && idx < l) {
						col = sortInfo[idx];
						sortFn = self.getSortFn(col, d);
						var propA = gridUtils.evalProperty(itemA, col.field);
						var propB = gridUtils.evalProperty(itemB, col.field);
						if (self.isCustomSort) {
							res = sortFn(propA, propB);
							tem = col.direction === ASC ? res : (res * -1);
						} else {
							if (propA === null || propB === null) {
								if (propB === null && propA === null) {
									tem = 0;
								} else if (propA === null) {
									tem = -1;
								} else if (propB === null) {
									tem = 1;
								}
							} else {
								res = sortFn(propA, propB);
								tem = col.direction === ASC ? res : (res * -1);
							}
						}
						idx++;
					}
					return tem;
				});
			};
			self.Sort = function(gridConfig, sortInfo, data) {
				//$log.debug('Sort');
				if (self.isSorting) {
					return;
				}
				self.isSorting = true;
				gridConfig.sorted = true;
				self.sortData(sortInfo, data);
				$timeout(function() {
					gridConfig.sorted = false;
				}, 100);
				self.isSorting = false;
			};
			return self;
		}
	]);

	function formatDates($filter, dateFormat, input) {
		if (typeof input !== 'object') {
			return input;
		}
		for (var key in input) {
			if (!input.hasOwnProperty(key)) {
				continue;
			}
			var value = input[key];
			if (typeof value === 'string' && (value.match(iso8601RegEx))) {
				input[key] = $filter('date')(value, dateFormat);
			} else if (typeof value === 'object') {
				formatDates($filter, dateFormat, value); //Recursive into object
			}
		}
	}
	angular.module('cargocommonApp').filter('aswGridFilter', ['$filter', '$log',
		function($filter, $log) {
			return function(data, wildcard, criteria, dateFormat) {
				if (angular.isDefined(data)) {
					var filterExpr = '';
					if (criteria.length > 0) {
						var colCriteria;
						var isDate;
						for (var i = 0; i < criteria.length; i++) {
							colCriteria = criteria[i];
							if (colCriteria.value !== '') {
								isDate = angular.isDate(colCriteria.value);
								if (isDate) {
									filterExpr = filterExpr.concat(' && Date.parse(').concat('item.').concat(colCriteria.property).concat(')');
								} else {
									filterExpr = filterExpr.concat(' && ').concat('item.').concat(colCriteria.property);
								}
								switch (colCriteria.operator) {
									case '=':
										if (isDate) {
											filterExpr = filterExpr.concat(' == ').concat(colCriteria.value.getTime());
										} else {
											filterExpr = filterExpr.concat(' == \'').concat(colCriteria.value).concat('\'');
										}
										break;
									case '<>':
										if (isDate) {
											filterExpr = filterExpr.concat(' != ').concat(colCriteria.value.getTime());
										} else {
											filterExpr = filterExpr.concat(' != \'').concat(colCriteria.value).concat('\'');
										}
										break;

									case '^':
										filterExpr = filterExpr.concat('.slice(0,\'').concat(colCriteria.value).concat('\'.length) == ').concat('\'').concat(colCriteria.value).concat('\'');
										break;
									case '$':
										filterExpr = filterExpr.concat('.slice(-\'').concat(colCriteria.value).concat('\'.length) == ').concat('\'').concat(colCriteria.value).concat('\'');
										break;
									case '*':
										filterExpr = filterExpr.concat('.indexOf(').concat('\'').concat(colCriteria.value).concat('\') !== -1');
										break;
									case '<':
									case '<=':
									case '>':
									case '>=':
										if (isDate) {
											filterExpr = filterExpr.concat(' ').concat(colCriteria.operator).concat(' ').concat(colCriteria.value.getTime());
										} else {
											filterExpr = filterExpr.concat(' ').concat(colCriteria.operator).concat(' \'').concat(colCriteria.value).concat('\'');
										}
										break;
									default:
										filterExpr = filterExpr.concat(' == \'').concat(colCriteria.value).concat('\'');
								}
							} else if (angular.isDefined(colCriteria.values) && colCriteria.values.length > 0) {
								filterExpr = filterExpr.concat(' && ').concat(JSON.stringify(colCriteria.values)).concat('.indexOf(item.').concat(colCriteria.property).concat(') !== -1');
							}
						}
						if (filterExpr.length > 0) {
							filterExpr = filterExpr.substring(4);
						}
					}
					//$log.debug('FilterExpr : ' + filterExpr);
					if (filterExpr.length > 0 || wildcard.length > 0) {
						var filtered = [];
						angular.forEach(data, function(item, key) {
							if (wildcard.length > 0) {
								var itemCopy = jQuery.extend(true, {}, item);
								formatDates($filter, dateFormat, itemCopy);
								var itemValue = JSON.stringify(itemCopy);
								if (itemValue.indexOf(wildcard) === -1) {
									return;
								}
							}
							if (filterExpr.length > 0) {
								if (!eval(filterExpr)) {
									return;
								}
							}
							filtered.push(item);
						});
						return filtered;
					}
				}
				return data;
			};
		}
	]);
})();
/*
 	aswGrid
 		data:
		totalRows:
		enablePaging:
		enableSorting:
		enableColumnResize:
		enableColumnReordering:
		pagingOptions:
		filterOptions:
		enableRowSelection:
		multiSelect:
		selectedItems:
		afterSelectionChange:
		selectCallBack:
		rowClassCallBack:
		showHeading:
		showFooter:
		showColumnMenu:
		settingsTemplate:
		moreActionTemplate:
		rowDetailTemplate:
		rowContextmenuTemplate:
		maxHeight:
		maxWidth:
		minHeight:
		showNoRecordFoundMsg
		noRecordFoundMsg:
		enableFiltering:
		enableGrouping:
		enablePinning:
		groupBy:
		groupByExpanded:
		columnDefs
		 	field:
		 	headerGroup:
		 	displayName:
		 	tooltip:
		 	headerClass
		 	headerCellTemplate:
		 	headerFilter
		 	cellClass:
		 	cellTemplate:
		 	cellFilter:
		 	sortable:
		 	groupable
		 	sortFn:
		 	visible:
		 	resizable:
		 	width:
		 	pinned:
		 	pinnable:
		 	ui:
		headerGroupDefs 
			'<headerGroup>': 
				showHideDetail:
				headerGroupTemplate: 
							
*/