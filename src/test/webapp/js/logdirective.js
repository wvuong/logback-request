/**
 * Created by will on 12/15/13.
 */
'use strict';

angular.module('com.willvuong.logback-request', [])
.directive('logTable', ['$document', '$window', function($document, $window) {
    return {
        restrict: 'E',
        scope: {
            name: '@'
        },
        replace: true,
        template: '<div id="logTableDirective" class="panel panel-default">' +
            '<div class="panel-heading">' +
            '<h3 class="panel-title" ng-click="toggle()">' +
            '<span class="glyphicon" ng-class="{\'glyphicon-collapse-down\': dialogIsHidden, \'glyphicon-collapse-up\': !dialogIsHidden}">&nbsp;</span>' +
            'Logging <input type="text" class="pull-right" ng-model="filterBy" placeholder="Filter..."/></h3></div>' +
            '<div class="panel-body" ng-hide="dialogIsHidden"><table class="table table-striped table-condensed table-bordered">' +
            '<thead><tr><th>time</th><th>level</th><th>logger</th><th>mdc</th><th>message</th></tr></thead>' +
            '<tbody><tr ng-repeat="log in logdata | filter: filterBy" class="{{log.level.levelStr | bootstrapifyPriority}}">' +
            '<td>{{log.timeStamp | date: "yyyy-MM-dd HH:mm:ss"}}</td>' +
            '<td><span class="label label-{{log.level.levelStr | bootstrapifyPriority}}">{{log.level.levelStr}}</span></td>' +
            '<td>{{log.loggerName}}</td>' +
            '<td><table class="table table-striped table-condensed table-bordered"><tr ng-repeat="(key, value) in log.mdc"><td>{{key}}</td><td>{{value}}</td></tr></table></td>' +
            '<td>{{log.formattedMessage}}</td></tr></tbody></table></div></div>',
        controller: function($scope, $element, $attrs, $transclude, $window, $timeout) {
            $scope.logdata = $window[$scope.name];
            $scope.dialogIsHidden = true;

            $scope.toggle = function() {
                $scope.dialogIsHidden = !$scope.dialogIsHidden;
            };
        },
        link: function(scope, element, attrs, ngModel) {
        }
    };
}])
.filter('bootstrapifyPriority', function() {
    return function(priority) {
        if (priority == 'ERROR') {
            return 'danger';
        }
        else if (priority == 'WARNING') {
            return 'warning'
        }
        else if (priority == 'INFO') {
            return 'info';
        }
        else if (priority == 'DEBUG') {
            return 'primary';
        }
        else if (priority == 'TRACE') {
            return 'default';
        }
    };
});