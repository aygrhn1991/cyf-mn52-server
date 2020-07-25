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
        $scope.nowYear=new Date().getFullYear();
        $scope.getLayoutData();
        $scope.url = 'http://image.mn52.com/img';
        $scope.search = window.Util.getSearchObject();
        $scope.search.number1 = -1;
        $scope.model = window.Util.copyObject($scope.pageModel);
        // $scope.getCategory();
        // $scope.get();
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