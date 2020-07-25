var app = angular.module('app', ['ngRoute']);
app.config(function ($routeProvider) {
    $routeProvider
        .when('/thumb', {
            templateUrl: '/admin/thumb',
            controller: 'thumbCtrl'
        })
        .when('/category', {
            templateUrl: '/admin/category',
            controller: 'categoryCtrl'
        })
        .when('/tag', {
            templateUrl: '/admin/tag',
            controller: 'tagCtrl'
        })
        .when('/ad', {
            templateUrl: '/admin/ad',
            controller: 'adCtrl'
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
        {id: 1, name: '图集管理', sort: 1, group_name: '图集管理', group_sort: 1, image: 'empty.jpg', path: '#/thumb'},
        {id: 2, name: '类别管理', sort: 2, group_name: '图集管理', group_sort: 2, image: 'empty.jpg', path: '#/category'},
        {id: 3, name: '标签管理', sort: 3, group_name: '图集管理', group_sort: 3, image: 'empty.jpg', path: '#/tag'},
        {id: 4, name: '广告管理', sort: 1, group_name: '广告管理', group_sort: 1, image: 'empty.jpg', path: '#/ad'},
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
            $scope.categoryOfSearch = [];
            $scope.categoryOfModal = [];
            data.data.forEach(function (x) {
                $scope.categoryOfSearch.push(x);
                $scope.categoryOfModal.push(x);
            })
            $scope.categoryOfSearch.unshift({id: -1, cat_name: '全部'});
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
        $scope.model = e;
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
        $scope.index = layer.open({
            title: '添加图集',
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
        if (window.Util.isNull($scope.model.cat_id) ||
            window.Util.isNull($scope.model.title) ||
            window.Util.isNull($scope.model.tag)) {
            layer.msg('请完图集信息');
            return;
        }
        var formData = new FormData();
        formData.append('cat_id', $scope.model.cat_id);
        formData.append('title', $scope.model.title);
        formData.append('tag', $scope.model.tag);
        formData.append('file', $('#cover')[0].files[0]);
        for (var i = 0; i < $('#img')[0].files.length; i++) {
            formData.append('files', $('#img')[0].files[i]);
        }
        $.ajax({
            type: 'post',
            url: '/api/addThumb',
            contentType: false,
            processData: false,
            dataType: 'json',//收到服务器数据的格式
            data: formData,
            success: function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.get();
                    $scope.closeModal();
                }
            },
        })
    };
    $scope.showEditModal = function (e) {
        $scope.model = e;
        // e.time_start = window.Util.dateToYYYYMMDDHHMMSS(new Date(e.time_start));
        // e.time_end = window.Util.dateToYYYYMMDDHHMMSS(new Date(e.time_end));
        // $scope.model = e;
        // layui.laydate.render({
        //     elem: '#date',
        //     type: 'datetime',
        //     value: e.time_start,
        //     done: function (value, date, endDate) {
        //         $scope.model.time_start = value;
        //     }
        // });
        $scope.index = layer.open({
            title: '修改图集信息',
            type: 1,
            content: $('#modal-edit'),
            shade: 0,
            area: '600px',
            maxHeight: 500,
            move: false,
            resize: false,
        });
    };
    $scope.edit = function () {
        if (window.Util.isNull($scope.model.title)) {
            layer.msg('请完善图集信息');
            return;
        }
        $http.post('/api/updateThumb', $scope.model).success(function (data) {
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
        layer.confirm('此操作将删除图集', null, function () {
            $http.post(`/api/deleteThumb/${e.id}`).success(function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.get();
                }
            });
        });
    };
    $scope.deleteImg = function (e) {
        layer.confirm('此操作将删除图集', null, function () {
            $http.post(`/api/deleteImg/${e.id}`).success(function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.showImageModal($scope.model);
                }
            });
        });
    }
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
        cat_id: null,
        title: null,
        tags: null,
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
app.controller('categoryCtrl', function ($scope, $http) {
    $scope.get = function () {
        $scope.search.loading = layer.load();
        $http.post('/api/getCategory', $scope.search).success(function (data) {
            layer.close($scope.search.loading);
            $scope.data = data.data;
            $scope.makePage(data);
        });
    };
    $scope.showAddModal = function () {
        $scope.model = window.Util.copyObject($scope.pageModel);
        $scope.index = layer.open({
            title: '添加类别',
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
        if (window.Util.isNull($scope.model.cat_url) ||
            window.Util.isNull($scope.model.cat_name) ||
            window.Util.isNull($scope.model.seotitle) ||
            window.Util.isNull($scope.model.keywords) ||
            window.Util.isNull($scope.model.description)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/addCategory', $scope.model).success(function (data) {
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
            title: '修改类别',
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
        if (window.Util.isNull($scope.model.cat_url) ||
            window.Util.isNull($scope.model.cat_name) ||
            window.Util.isNull($scope.model.seotitle) ||
            window.Util.isNull($scope.model.keywords) ||
            window.Util.isNull($scope.model.description)) {
            layer.msg('请完善信息');
            return;
        }
        $http.post('/api/updateCategory', $scope.model).success(function (data) {
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
        layer.confirm('此操作将更改类别状态', null, function () {
            $http.post(`/api/updateCategoryState/${e.id}/${f}`).success(function (data) {
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
        cat_url: null,
        cat_name: null,
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