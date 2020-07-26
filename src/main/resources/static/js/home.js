var app = angular.module('app', []);
app.controller('layoutCtrl', function ($scope, $http) {
    $scope.getLayoutData = function () {
        $http.post('/home/getLayoutData').success(function (data) {
            $scope.nav = [];
            $scope.category = [];
            data.data.category.forEach(function (x) {
                $scope.nav.push(x);
                $scope.category.push(x);
            });
            $scope.category.unshift({id: -1, name: '全站搜索'});
            $scope.tag = data.data.tag;
        });
    };
    $scope.reset = function () {
        $scope.nowYear = new Date().getFullYear();
        $scope.getLayoutData();
        $scope.search = window.Util.getSearchObject();
        $scope.search.number1 = -1;
    };
    $scope.reset();
});
app.controller('indexCtrl', function ($scope, $http) {
    $scope.getIndexData = function () {
        $http.post('/home/getIndexData').success(function (data) {
            console.log(data.data);
            $scope.category = data.data.category;
        });
    };
    $scope.reset = function () {
        $scope.getIndexData();
    };
    $scope.reset();
});
app.controller('tagCtrl', function ($scope, $http) {
    $scope.get = function () {
        $scope.search.loading = layer.load();
        $http.post('/api/getTag', $scope.search).success(function (data) {
            layer.close($scope.search.loading);
            $scope.data = data.data;
            $scope.makePage(data);
        });
    };
    $scope.showAddModal = function () {
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.index = layer.open({
            title: '添加标签',
            type: 1,
            content: $('#modal'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.add = function () {
        if (window.Util.isNull($scope.model.tag) ||
            window.Util.isNull($scope.model.seotitle) ||
            window.Util.isNull($scope.model.keywords) ||
            window.Util.isNull($scope.model.description)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/addTag', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showEditModal = function (e) {
        $scope.model = e;
        $scope.index = layer.open({
            title: '修改标签',
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
        if (window.Util.isNull($scope.model.tag) ||
            window.Util.isNull($scope.model.seotitle) ||
            window.Util.isNull($scope.model.keywords) ||
            window.Util.isNull($scope.model.description)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/updateTag', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.closeModal = function () {
        layer.close($scope.index);
    };
    $scope.editState = function (e, f) {
        layer.confirm('此操作将更改标签状态', null, function () {
            $http.post(`/api/updateTagState/${e.id}/${f}`).success(function (data) {
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
        tag: null,
        seotitle: null,
        keywords: null,
        description: null,
    };
    $scope.reset = function () {
        $scope.search = window.Util.getSearchObject();
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.get();
    };
    $scope.reset();
});
app.controller('adCtrl', function ($scope, $http) {
    $scope.get = function () {
        $scope.search.loading = layer.load();
        $http.post('/api/getAd', $scope.search).success(function (data) {
            layer.close($scope.search.loading);
            $scope.data = data.data;
            $scope.makePage(data);
        });
    };
    $scope.showAddModal = function () {
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.index = layer.open({
            title: '添加广告',
            type: 1,
            content: $('#modal'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.add = function () {
        if (window.Util.isNull($scope.model.code) ||
            window.Util.isNull($scope.model.position_desc)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/addAd', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.showEditModal = function (e) {
        $scope.model = e;
        $scope.index = layer.open({
            title: '修改广告',
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
        if (window.Util.isNull($scope.model.code) ||
            window.Util.isNull($scope.model.position_desc)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/updateAd', $scope.model).success(function (data) {
            layer.msg(data.message);
            if (data.success) {
                $scope.get();
                $scope.closeModal();
            }
        });
    };
    $scope.closeModal = function () {
        layer.close($scope.index);
    };
    $scope.editState = function (e, f) {
        layer.confirm('此操作将更改广告状态', null, function () {
            $http.post(`/api/updateAdState/${e.id}/${f}`).success(function (data) {
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
        code: null,
        position_desc: null,
    };
    $scope.reset = function () {
        $scope.search = window.Util.getSearchObject();
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.get();
    };
    $scope.reset();
});