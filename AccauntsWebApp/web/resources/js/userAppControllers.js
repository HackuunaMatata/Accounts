/* --- Controllers for user page --- */
var AppForUser = angular.module('AppForUser',['ngRoute']);

var CrossModuleData = {};

AppForUser.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/',{
            templateUrl:'templates/AppForUser/login.html',
            controller : 'LoginUserController'
        })
        .when('/specifyUserId',{
            templateUrl:'templates/AppForUser/specifyUserId.html',
            controller:'SpecifyUserIdController'
        })
        .when('/showUserInfo',{
            templateUrl:'templates/AppForUser/userInfoTable.html',
            controller :'UserInfoTableController' 
        })
        .when('/successPage', {
            templateUrl:'templates/AppForUser/successPage.html',
            controller:'SuccessPageController'
        })
        .otherwise({
            redirectTo:'/'
        });
}]);


AppForUser.controller('AppForUserController', ['$scope', function ($scope) {
    CrossModuleData = {};
}]);


AppForUser.controller('LoginUserController', ['$scope','$http',function ($scope, $http) {
    $scope.userdata = {};
    $scope.isSubmit = false;
    CrossModuleData = {};
    
    $scope.userdata.name = "Вася";
    $scope.userdata.surname = "Пупкин";
    $scope.userdata.isNewUser = false;
    
    $scope.sendData = function (loginForm) {
        if (loginForm.$valid && !$scope.isSubmit) {
            $scope.isSubmit = true;
            var sendInfo = {};
            sendInfo.goal = "getUserInfo";
            sendInfo.data = $scope.userdata;
            $http.post("/userWork", sendInfo).success(
                function (answer) {
                    CrossModuleData.answer = answer;
                    
                    if (answer.goal == "specifyUserId") {
                        document.location.href = '#/specifyUserId';
                    } else if (answer.goal == "showUserInfo") {
                        document.location.href = '#/showUserInfo';
                    }
                }
            ).error(function () {
                CrossModuleData.answer = {};
                alert("Inner server error or disconnect...");
            })
        }
    };
    
    $scope.setPattern = function () {
        return dataFormats.chars;
    };
}]);

AppForUser.controller('SpecifyUserIdController', ['$scope','$http', function ($scope, $http) {
    $scope.userInfo = CrossModuleData.answer;

    $scope.sendChoise = function (user_id) {
        $scope.userInfo.id = user_id;
        delete $scope.userInfo.ids;
        var sendInfo = {};
        sendInfo.goal = "specifyUserId";
        sendInfo.data = $scope.userInfo;
        $http.post("/userWork", sendInfo).success(
            function (answer) {
                CrossModuleData.answer = answer;
                document.location.href = '#/showUserInfo';
            }
        ).error(function () {
            CrossModuleData = {};
            alert("Inner server error or disconnect...");
            document.location.href = '#/';
        })
    }
}]);


// See app.js for some info (index2formatName, dataFormats, etc.)
AppForUser.controller('UserInfoTableController', ['$scope','$http',function ($scope, $http) {
    $scope.dataTable = CrossModuleData.answer.data;
    $scope.userName = CrossModuleData.answer.name;
    $scope.userSurname = CrossModuleData.answer.surname;
    $scope.userId = CrossModuleData.answer.id;
    $scope.isSubmit = false;

    $scope.updateData = function (userDataForm) {
        if (userDataForm.$valid && !$scope.isSubmit) {
            $scope.isSubmit = true;
            var sendInfo = {};
            sendInfo.goal = "updateUserInfo";
            sendInfo.data = $scope.dataTable;
            sendInfo.id = CrossModuleData.answer.id;
            $http.post("/userWork", sendInfo).success(
                function (answer) {
                    CrossModuleData.answer = answer;
                    document.location.href = '#/successPage';
                }
            ).error(function () {
                CrossModuleData = {};
                alert("Inner server error or disconnect...");
                document.location.href = '#/';
            })
        }
    };
    
    $scope.isList = function (index) {
        return ($scope.dataTable[index].type == dataTypes.list);
    };
    
    $scope.isContainError = function (index) {
        return typeof $scope.dataTable[index].value == "undefined";
    };

    $scope.getPattern = function (formatIndex) {
        var formatName = index2formatName[formatIndex];
        return dataFormats[formatName];
    };
    
    $scope.getErrorMessage = function (formatIndex) {
        return errorMessages[formatIndex];
    };
}]);


AppForUser.controller('SuccessPageController', ['$scope', function ($scope) {
    $scope.result = CrossModuleData.answer;

    $scope.isSuccess = function () {
        return $scope.result.status == 0;
    }
}]);
