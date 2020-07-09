var app = angular.module('app', ['ngRoute']);
app.config(function ($routeProvider) {
    $routeProvider
        .when('/thumb', {
            templateUrl: '/admin/thumb',
            controller: 'thumbCtrl'
        })
        .when('/user', {
            templateUrl: '/admin/user/user',
            controller: 'thumbCtrl'
        })
        .when('/welcome', {
            templateUrl: '/admin/welcome'
        })
        .when('/unauthorized', {
            templateUrl: '/error/unauthorized'
        })
        .otherwise({
            redirectTo: '/welcome'
        });
});
app.run(function ($rootScope, $http, $location) {
    var systemPage = ['#/welcome', '#/password'];
    var pages = [
        {id: 1, name: '图片管理', sort: 1, group_name: '图片管理', group_sort: 1, image: 'empty.jpg', path: '#/thumb'},
    ];
    $rootScope.getAdmin = function () {
        $http.post('/admin/getAdmin').success(function (data) {
            $rootScope.admin = data.data;
            window.Util.setCookie('admin', JSON.stringify(data.data));
            $rootScope.menu = [];
            var set = new Set();
            pages.forEach(function (x) {
                set.add(x.group_name);
            });
            Array.from(set).forEach(function (x) {
                var menu = {name: x, select: false, pages: []};
                pages.forEach(function (y) {
                    if (y.group_name == x) {
                        menu.pages.push(y);
                    }
                })
                $rootScope.menu.push(menu);
            })
            layui.use('element', function () {
                var element = layui.element;
            });
            $rootScope.matchMenu();
            $rootScope.startListener();
        });
    };
    if (!window.Util.isNull(window.Util.getCookie('admin'))) {
        $rootScope.getAdmin();
    } else {
        window.location.href = '/admin/login';
    }
    $rootScope.matchMenu = function () {
        var hasPage = false;
        var path = '#' + $location.path();
        $rootScope.menu.forEach(function (x) {
            x.select = false;
            x.pages.forEach(function (y) {
                y.select = false;
                if (y.path == path) {
                    y.select = true;
                    x.select = true;
                    hasPage = true;
                }
            })
        })
        if (!hasPage) {
            for (var i = 0; i < systemPage.length; i++) {
                if (path == systemPage[i]) {
                    return;
                }
            }
            $location.path('/unauthorized');
        }
    };
    $rootScope.menuClick = function (e) {
        $rootScope.menu.forEach(function (x) {
            x.select = false;
        });
        e.select = true;
    };
    $rootScope.logout = function () {
        window.Util.removeCookie('admin');
        window.location.href = '/admin/login';
    };
    $rootScope.startListener = function () {
        $rootScope.$on('$routeChangeStart', function (event, next, current) {
            $rootScope.matchMenu();
        });
    };
});
app.controller('thumbCtrl', function ($scope, $http) {
    $scope.getCategory = function () {
        $scope.search.loading = layer.load();
        $http.post('/api/common/getCategory').success(function (data) {
            $scope.category = data.data;
            $scope.category.unshift({id: -1, cat_name: '全部'});
        });
    };
    $scope.get = function () {
        $scope.search.loading = layer.load();
        $http.post('/api/getThumb', $scope.search).success(function (data) {
            layer.close($scope.search.loading);
            $scope.data = data.data;
            $scope.makePage(data);
        });
    };
    $scope.showImageModal = function (e) {
        $http.post(`/api/getThumbGallery/${e.unique_id}`).success(function (data) {
            $scope.image = data.data;
        });
        $scope.index = layer.open({
            title: '图片浏览',
            type: 1,
            content: $('#modal-image'),
            shade: 0,
            area: ['600px', '500px'],
            offset: '100px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.showAddModal = function () {
        $scope.model = window.Util.copyObject($scope.pageModel);
        layui.laydate.render({
            elem: '#date',
            type: 'datetime',
            value: $scope.model.time_start = window.Util.dateToYYYYMMDDHHMMSS(new Date()),
            done: function (value, date, endDate) {
                $scope.model.time_start = value;
            }
        });
        $scope.index = layer.open({
            title: '添加生产计划',
            type: 1,
            content: $('#modal'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.calculateEndTime = function () {
        if (window.Util.isNull($scope.model.count_plan) || $scope.model.count_plan == 0 ||
            window.Util.isNull($scope.model.extra_hour) ||
            window.Util.isNull($scope.model.speed) || $scope.model.speed == 0) {
            layer.msg('数据错误，无法计算');
            return;
        }
        var timestamp = window.Util.stringToDate($scope.model.time_start).getTime();
        timestamp += $scope.model.extra_hour * 3600 * 1000;
        timestamp += $scope.model.count_plan / $scope.model.speed * 3600 * 1000;
        $scope.model.time_end = window.Util.dateToYYYYMMDDHHMMSS(new Date(timestamp));
    };
    $scope.add = function () {
        $scope.calculateEndTime();
        if (window.Util.isNull($scope.model.model) ||
            window.Util.isNull($scope.model.order) ||
            window.Util.isNull($scope.model.batch) ||
            window.Util.isNull($scope.model.line) ||
            window.Util.isNull($scope.model.card) ||
            window.Util.isNull($scope.model.count_plan) || $scope.model.count_plan == 0 ||
            window.Util.isNull($scope.model.time_start) ||
            window.Util.isNull($scope.model.time_end) ||
            window.Util.isNull($scope.model.extra_hour) ||
            window.Util.isNull($scope.model.speed) || $scope.model.speed == 0) {
            layer.msg('请完善生产计划信息');
            return;
        }
        $http.post('/api/addPatchPlan', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showEditModal = function (e) {
        e.time_start = window.Util.dateToYYYYMMDDHHMMSS(new Date(e.time_start));
        e.time_end = window.Util.dateToYYYYMMDDHHMMSS(new Date(e.time_end));
        $scope.model = e;
        layui.laydate.render({
            elem: '#date',
            type: 'datetime',
            value: e.time_start,
            done: function (value, date, endDate) {
                $scope.model.time_start = value;
            }
        });
        $scope.index = layer.open({
            title: '修改生产计划',
            type: 1,
            content: $('#modal'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.edit = function () {
        $scope.calculateEndTime();
        if (window.Util.isNull($scope.model.line) ||
            window.Util.isNull($scope.model.card) ||
            window.Util.isNull($scope.model.count_plan) || $scope.model.count_plan == 0 ||
            window.Util.isNull($scope.model.time_start) ||
            window.Util.isNull($scope.model.time_end) ||
            window.Util.isNull($scope.model.extra_hour) ||
            window.Util.isNull($scope.model.speed) || $scope.model.speed == 0) {
            layer.msg('请完善生产计划信息');
            return;
        }
        $http.post('/api/updatePatchPlan', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showEditStepModal = function (e) {
        $scope.model = e;
        $scope.stepModel = {plan_id: e.id, step: null, message: null};
        $scope.index = layer.open({
            title: '生产计划进度更新',
            type: 1,
            content: $('#modal-step-edit'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.editStep = function (next) {
        if ($scope.model.step == 0) {
            $scope.stepModel.step = 1;
        } else if ($scope.model.step == 1 || $scope.model.step == 10) {
            $scope.stepModel.step = next ? 2 : 10;
        } else if ($scope.model.step == 2 || $scope.model.step == 20) {
            $scope.stepModel.step = next ? 3 : 20;
        } else if ($scope.model.step == 3) {
            $scope.stepModel.step = 4;
        } else {
            $scope.stepModel.step = null;
        }
        $http.post('/api/updatePatchPlanStep', $scope.stepModel).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showFinishModal = function (e) {
        $scope.model = e;
        $scope.index = layer.open({
            title: '生产计划结转',
            type: 1,
            content: $('#modal-finish'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.finish = function () {
        if (window.Util.isNull($scope.model.count_finish) ||
            $scope.model.count_finish == 0 ||
            $scope.model.count_finish > $scope.model.count_plan) {
            layer.msg('请完善生产计划结转信息');
            return;
        }
        $http.post('/api/finishPatchPlan', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showStepModal = function (e) {
        $http.post(`/api/getPatchPlanStep/${e.id}`).success(function (data) {
            $scope.planStep = data.data;
            $scope.index = layer.open({
                title: '生产计划进度',
                type: 1,
                content: $('#modal-step'),
                shade: 0,
                area: ['600px', '500px'],
                offset: '100px',
                maxHeight: 500,
                move: false,
                resize: false,
            });
        })
    };
    $scope.closeModal = function () {
        layer.close($scope.index);
    };
    $scope.delete = function (e) {
        layer.confirm('此操作将删除生产计划', null, function () {
            $http.post(`/api/deletePatchPlan/${e.id}`).success(function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.get();
                }
            });
        });
    };
    $scope.makePage = function (data) {
        layui.laypage.render({
            elem: 'page',
            count: data.count,
            curr: $scope.search.page,
            limit: $scope.search.limit,
            limits: [10, 20, 30, 40, 50],
            layout: ['prev', 'page', 'next', 'count', 'limit'],
            jump: function (obj, first) {
                $scope.search.page = obj.curr;
                $scope.search.limit = obj.limit;
                if (!first) {
                    $scope.get();
                }
            }
        });
    };
    $scope.pageModel = {
        id: null,
        model: null,
        order: null,
        batch: null,
        line: null,
        card: null,
        count_plan: null,
        count_finish: null,
        time_start: null,
        time_end: null,
        extra_hour: null,
        speed: null,
        step: null,
        mark_plan: null,
        mark_finish: null,
    };
    $scope.reset = function () {
        $scope.url = 'http://image.mn52.com/img';
        $scope.search = window.Util.getSearchObject();
        $scope.search.number1 = -1;
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.getCategory();
        $scope.get();
    };
    $scope.reset();
});