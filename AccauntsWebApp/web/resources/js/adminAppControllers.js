/* --- Controllers for admin page --- */
var AppForAdmin = angular.module('AppForAdmin',['ngRoute']);
var CrossModuleData = {};


AppForAdmin.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/',{
            templateUrl:'templates/AppForAdmin/homeAdmin.html',
            controller : 'HomeAdminController'
        })
        .when('/questionsEditor',{
            templateUrl:'templates/AppForAdmin/questionsEditor.html',
            controller:'QuestionsEditorController'
        })
        .when('/listEditor', {
            templateUrl:'templates/AppForAdmin/listEditor.html',
            controller:'ListEditorController'
        })
        .when('/getInfo', {
            templateUrl:'templates/AppForAdmin/getInfoAboutUsers.html',
            controller:'GetInfoAboutUsersController'
        })
        .otherwise({
            redirectTo:'/'
        });
}]);



AppForAdmin.controller('HomeAdminController', ['$scope', function ($scope) {
    $scope.msg = "";
    if (typeof CrossModuleData.msg != "undefined")
        $scope.msg = CrossModuleData.msg;
}]);


AppForAdmin.controller('QuestionsEditorController', ['$scope','$http', function ($scope, $http) {
    CrossModuleData = {};
    
    $scope.questions = {};
    $scope.isDoRequest = false;
    $scope.index2formatName = index2formatName;
    $scope.typeNames = typeNames;
    $scope.newQuestion = {};
    
    var getQuestions = function () {
        $scope.isDoRequest = true;
        var sendInfo = {};
        sendInfo.goal = 'getQuestionsList';
        
        $http.post("/adminWork", sendInfo).success(
            function (answer) {
                $scope.questions = answer.questions;
                for (var n in $scope.questions) {
                    $scope.questions[n].format = index2formatName[$scope.questions[n].format];
                    $scope.questions[n].type = typeNames[$scope.questions[n].type];
                }
                $scope.isDoRequest = false;
            }
        ).error(function () {
            alert("Inner server error or disconnect...");
            document.location.href = '#/';
        })
    };
    getQuestions();
    
    $scope.isReadOnly = function (state) {
        return state == 0;
    };
    
    $scope.addNewQuestion = function () {
        $scope.newQuestion.change = 1;
        $scope.newQuestion.id = -1;
        $scope.questions[$scope.questions.length] = $scope.newQuestion;
        $scope.newQuestion = {};
    };

    $scope.isInvalidNewQuestion = function () {
        return typeof $scope.newQuestion.type == "undefined";
    };

    $scope.updateQuestions = function (form) {
        if (form.$valid) {
            $scope.isDoRequest = true;
            var sendInfo = {};
            sendInfo.goal = 'updateQuestionsList';

            for (var n in $scope.questions) {
                $scope.questions[n].format = index2formatName.indexOf($scope.questions[n].format);
                $scope.questions[n].type = typeNames.indexOf($scope.questions[n].type);
            }
            sendInfo.data = $scope.questions;
            
            $http.post("/adminWork", sendInfo).success(
                function (answer) {
                    CrossModuleData = answer;
                    $scope.isDoRequest = false;
                    document.location.href = '#/';
                }
            ).error(function () {
                alert("Inner server error or disconnect...");
                document.location.href = '#/';
            })
        }
    };
}]);


AppForAdmin.controller('ListEditorController', ['$scope','$http', function ($scope, $http) {
    $scope.isDoRequest = false;
    $scope.list = {};
    $scope.list.selected = {};
    $scope.positions = {};
    $scope.positions.isEmpty = true;
    $scope.positions.newPosition = "";
    $scope.msg = "";
    var needBlock = false;

    var getListNames = function () {
        $scope.isDoRequest = true;
        var sendInfo = {};
        sendInfo.goal = 'getListNames';

        $http.post("/adminWork", sendInfo).success(
            function (answer) {
                $scope.list = answer;
                $scope.list.selected = {};
                $scope.isDoRequest = false;
            }
        ).error(function () {
            alert("Inner server error or disconnect...");
            document.location.href = '#/';
        })
    };
    getListNames();

    $scope.isSelectList = function () {
        return typeof $scope.list.selected.id != "undefined";
    };

    $scope.getSelectedList = function () {
        if ($scope.isDoRequest == false && !needBlock) {
            needBlock = true;
            $scope.msg = "";
            var sendInfo = {};
            sendInfo.goal = 'getListContent';
            sendInfo.idQuestion = $scope.list.selected.id;

            $http.post("/adminWork", sendInfo).success(
                function (answer) {
                    $scope.positions = answer;
                    $scope.positions.isEmpty = false;
                    $scope.positions.newPosition = "";
                    needBlock = false;
                }
            ).error(function () {
                alert("Inner server error or disconnect...");
                document.location.href = '#/';
            })
        }
    };

    $scope.addNewPosition = function () {
        if ( !$scope.isInvalidNewPosition() ) {
            $scope.positions.texts[$scope.positions.texts.length] = $scope.positions.newPosition;
            $scope.positions.newPosition = "";
        }
    };
    
    $scope.isInvalidNewPosition = function () {
        return $scope.positions.newPosition == "";
    };
    
    $scope.isMsgEmpty = function () {
        return $scope.msg == "";
    }

    $scope.updateListContent = function () {
        if (!needBlock) {
            needBlock = true;
            var sendInfo = {};
            sendInfo.goal = 'updateList';
            sendInfo.data = {};
            sendInfo.data.id = $scope.list.selected.id;
            sendInfo.data.text = $scope.positions.texts;

            $http.post("/adminWork", sendInfo).success(
                function (answer) {
                    $scope.msg = answer.msg;
                    needBlock = false;
                }
            ).error(function () {
                alert("Inner server error or disconnect...");
                document.location.href = '#/';
            })
        }
    };
}]);


AppForAdmin.controller('GetInfoAboutUsersController', ['$scope','$http', function ($scope, $http) {
    $scope.isDoRequest = false;
    $scope.users = [];
    $scope.selectUsers = {};
    $scope.data = {};
    $scope.xlsFilePath = "";
    
    var getUsers = function () {
        $scope.isDoRequest = true;
        var sendInfo = {};
        sendInfo.goal = 'getUsersListAndMailSettings';
        $http.post("/adminWork", sendInfo).success(
            function (answer) {
                $scope.users = answer.users;
                $scope.email = answer.email;
                $scope.email.masDelaysForSend  = timeForDelay.slice();
                $scope.email.masDelaysForControl = timeForDelay.slice(1);
                $scope.isDoRequest = false;
            }
        ).error(function () {
            alert("Inner server error or disconnect...");
            document.location.href = '#/';
        })
    };
    getUsers();
    
    
    $scope.getXLS = function () {
        if (!$scope.getXLS.doRequest) {
            $scope.getXLS.doRequest = true;
            var sendInfo = {};
            sendInfo.goal = 'sendUsersInfo';
            sendInfo.data = $scope.selectUsers;
            $http.post("/adminWork", sendInfo).success(
                function (answer) {
                    $scope.xlsFilePath = answer.filepath;
                    $scope.getXLS.doRequest = false;
                    document.location.href = $scope.xlsFilePath ;
                }
            ).error(function () {
                alert("Inner server error or disconnect...");
                document.location.href = '#/';
            })
        }
        
    };
    $scope.getXLS.doRequest = false;
    $scope.getXLS.isDoRequest = function () {
        return $scope.getXLS.doRequest == true;
    }

    $scope.isXlsFilePath = function () {
        return $scope.xlsFilePath != "";
    };

    // email configuration
    var timeForDelay = [
        {name : 'one time',   value : -1,            forGroup : 'other'},
        {name : 'never',      value : 0,             forGroup : 'other'},
        {name : '1 min',      value : 1,             forGroup : 'minutes'},
        {name : '2 mins',     value : 2,             forGroup : 'minutes'},
        {name : '5 mins',     value : 5,             forGroup : 'minutes'},
        {name : '10 min',     value : 10,            forGroup : 'minutes'},
        {name : '20 min',     value : 20,            forGroup : 'minutes'},
        {name : '30 min',     value : 30,            forGroup : 'minutes'},
        {name : '1 h',        value : 60,            forGroup : 'hours'},
        {name : '2 h',        value : (2*60),        forGroup : 'hours'},
        {name : '5 h',        value : (5*60),        forGroup : 'hours'},
        {name : '12 h',       value : (12*60),       forGroup : 'hours'},
        {name : '18 h',       value : (18*60),       forGroup : 'hours'},
        {name : '24 h',       value : (24*60),       forGroup : 'hours'},
        {name : '36 h',       value : (36*60),       forGroup : 'hours'},
        {name : '48 h',       value : (48*60),       forGroup : 'hours'},
        {name : '60 h',       value : (60*60),       forGroup : 'hours'},
        {name : '72 h',       value : (72*60),       forGroup : 'hours'},
        {name : '1 w',        value : (1*7*24*60),   forGroup : 'weeks'},
        {name : '2 w',        value : (2*7*24*60),   forGroup : 'weeks'},
        {name : '3 w',        value : (3*7*24*60),   forGroup : 'weeks'},
        {name : '4 w',        value : (4*7*24*60),   forGroup : 'weeks'},
        {name : '1 d',        value : (1*24*60),     forGroup : 'days'},
        {name : '2 d',        value : (2*24*60),     forGroup : 'days'},
        {name : '5 d',        value : (5*24*60),     forGroup : 'days'},
        {name : '7 d',        value : (7*24*60),     forGroup : 'days'},
        {name : '30 d',       value : (30*24*60),    forGroup : 'days'},
        {name : '45 d',       value : (45*24*60),    forGroup : 'days'},
        {name : '60 d',       value : (60*24*60),    forGroup : 'days'},
        {name : '90 d',       value : (90*24*60),    forGroup : 'days'},
        {name : '180 d',      value : (180*24*60),   forGroup : 'days'},
        {name : '270 d',      value : (270*24*60),   forGroup : 'days'},
        {name : '360 d',      value : (360*24*60),   forGroup : 'days'},
        {name : '1 y',        value : (1*365*24*60), forGroup : 'years'},
        {name : '2 y',        value : (2*365*24*60), forGroup : 'years'},
        {name : '5 y',        value : (5*365*24*60), forGroup : 'years'}
    ];
    
    $scope.email = {};
    $scope.email.address = "";
    $scope.email.enableTransmit    = false;
    $scope.email.delayMinForSend      = 0;
    $scope.email.delayMinForControl   = 0;
    $scope.email.masDelaysForSend  = [];
    $scope.email.masDelaysForControl = [];
    
    $scope.getPattern = function (formatName) {
        return dataFormats[formatName];
    };

    $scope.getErrorMessage = function (formatName) {
        var formatIndex = index2formatName.indexOf(formatName);
        return errorMessages[formatIndex];
    };

    $scope.isAddressNotExist = function () {
        return typeof $scope.email.address == "undefined" || $scope.email.address == "";
    };

    $scope.updateMailManager = function () {
        var sendInfo = {};
        sendInfo.goal = 'updateMailManagerSettings';
        sendInfo.data = {};
        sendInfo.data.address = $scope.email.address;
        sendInfo.data.enableTransmit = $scope.email.enableTransmit;
        sendInfo.data.delayMinForSend = $scope.email.delayMinForSend;
        sendInfo.data.delayMinForControl = $scope.email.delayMinForControl;
        $http.post("/adminWork", sendInfo).success(
            function (answer) {
                CrossModuleData = answer;
                document.location.href = '#/';
            }
        ).error(function () {
            alert("Inner server error or disconnect...");
            document.location.href = '#/';
        })
    }
}]);
