window.Util = {
    //#region 时间日期
    dateToYYYYMMDDHHMMSS: function (date) {
        var y = date.getFullYear();
        var M = ((date.getMonth() + 1) >= 10 ? '' : '0') + (date.getMonth() + 1);
        var d = (date.getDate() >= 10 ? '' : '0') + date.getDate();
        var h = (date.getHours() >= 10 ? '' : '0') + date.getHours();
        var m = (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes();
        var s = (date.getSeconds() >= 10 ? '' : '0') + date.getSeconds();
        return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
    },
    dateToYYYYMMDD: function (date) {
        var y = date.getFullYear();
        var M = ((date.getMonth() + 1) >= 10 ? '' : '0') + (date.getMonth() + 1);
        var d = (date.getDate() >= 10 ? '' : '0') + date.getDate();
        return y + '-' + M + '-' + d;
    },
    dateToMMDD: function (date) {
        var M = ((date.getMonth() + 1) >= 10 ? '' : '0') + (date.getMonth() + 1);
        var d = (date.getDate() >= 10 ? '' : '0') + date.getDate();
        return M + '-' + d;
    },
    dateToHHMMSS: function (date) {
        var h = (date.getHours() >= 10 ? '' : '0') + date.getHours();
        var m = (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes();
        var s = (date.getSeconds() >= 10 ? '' : '0') + date.getSeconds();
        return h + ':' + m + ':' + s;
    },
    dateToHHMM: function (date) {
        var h = (date.getHours() >= 10 ? '' : '0') + date.getHours();
        var m = (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes();
        return h + ':' + m;
    },
    dateToWeekday: function (date) {
        var days = '日一二三四五六';
        return '星期' + days.charAt(date.getDay());
    },
    stringToDate: function (str) {
        var date = new Date(str.replace(/-/g, '/'));
        return date;
    },
    addYear: function (date, year) {
        var y = date.getFullYear();
        return new Date(date.setFullYear(y + year));
    },
    addMonth: function (date, month) {
        var d1 = date.getDate();
        date.setDate(1);
        date.setMonth(date.getMonth() + month);
        var d2 = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
        date.setDate(Math.min(d1, d2));
        return new Date(date);
    },
    addDay: function (date, day) {
        var d = date.getDate();
        return new Date(date.setDate(d + day));
    },
    getYear: function (date) {
        var y = date.getFullYear();
        return y;
    },
    getMonth: function (date) {
        var M = date.getMonth() + 1;
        return M;
    },
    getDate: function (date) {
        var d = date.getDate();
        return d;
    },
    getHour: function (date) {
        var h = date.getHours();
        return h;
    },
    getMinute: function (date) {
        var m = date.getMinutes();
        return m;
    },
    getSecond: function (date) {
        var s = date.getSeconds();
        return s;
    },
    getDayStart: function (date) {
        return new Date((date.getFullYear()) + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' 00:00:00');
    },
    getDayEnd: function (date) {
        return new Date((date.getFullYear()) + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' 23:59:59');
    },
    getWeekStartDay: function (date) {
        var weekday = date.getDay() || 7;
        date.setDate(date.getDate() - weekday + 1);
        return new Date(date);
    },
    getWeekEndDay: function (date) {
        var endday = this.addDay(this.getWeekStartDay(date), 6);
        return endday;
    },
    getMonthStartDay: function (date) {
        date.setDate(1);
        return new Date(date);
    },
    getMonthEndDay: function (date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d1 = new Date(y, m, 1);
        var d2 = this.addDay(d1, -1);
        return d2;
    },
    getYearStartDay: function (date) {
        date.setMonth(0);
        date.setDate(1);
        return new Date(date);
    },
    getYearEndDay: function (date) {
        date.setMonth(11);
        date.setDate(31);
        return new Date(date);
    },
    secondToHHMMSS: function (seconds) {
        var temp = 0;
        var str = '';
        temp = parseInt(seconds / 3600 + '');
        str += (temp < 10) ? '0' + temp + ':' : '' + temp + ':';
        temp = parseInt(seconds % 3600 / 60 + '');
        str += (temp < 10) ? '0' + temp + ':' : '' + temp + ':';
        temp = parseInt(seconds % 3600 % 60 + '');
        str += (temp < 10) ? '0' + temp : '' + temp;
        return str;
    },
    //#endregion

    //#region 随机数
    getBooleanRandom: function () {
        var i = Math.floor(Math.random() * (1 - 0 + 1) + 0);
        return i === 0 ? false : true;
    },
    getIntRandom: function (min, max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    },
    getStringRandom: function (length, seed) {
        var s = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        if (arguments[1]) {
            s = seed;
        }
        var str = '';
        for (var i = 0; i < length; i++) {
            str += s.charAt(Math.floor(Math.random() * s.length));
        }
        return str;
    },
    //#endregion

    //#region web
    setCookie: function (name, value) {
        document.cookie = name + '=' + encodeURIComponent(value) + ';path=/';
    },
    getCookie: function (name) {
        var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
        var arr = document.cookie.match(reg);
        if (arr) {
            return decodeURIComponent(arr[2]);
        } else {
            return null;
        }
    },
    removeCookie: function (name) {
        document.cookie = name + '=' + encodeURIComponent('') + ';path=/;expires=' + new Date().toGMTString();
    },
    isInnerIp: function (url) {
        var reg1 = /(http|ftp|https|www):\/\//g;
        url = url.replace(reg1, '');
        var reg2 = /\:+/g;
        url = url.replace(reg2, '.');
        var urlArr = url.split('.');
        var ip = urlArr[0] + '.' + urlArr[1] + '.' + urlArr[2] + '.' + urlArr[3];
        var ipNum = this._getIpNum(ip);
        var aBegin = this._getIpNum('10.0.0.0');
        var aEnd = this._getIpNum('10.255.255.255');
        var bBegin = this._getIpNum('172.16.0.0');
        var bEnd = this._getIpNum('172.31.255.255');
        var cBegin = this._getIpNum('192.168.0.0');
        var cEnd = this._getIpNum('192.168.255.255');
        var dBegin = this._getIpNum('127.0.0.0');
        var dEnd = this._getIpNum('127.255.255.255');
        return ((ipNum >= aBegin) && (ipNum <= aEnd)) ||
            ((ipNum >= bBegin) && (ipNum <= bEnd)) ||
            ((ipNum >= cBegin) && (ipNum <= cEnd)) ||
            ((ipNum >= dBegin) && (ipNum <= dEnd));
    },
    _getIpNum: function (ip) {
        var ipArr = ip.split('.');
        var a = parseInt(ipArr[0]);
        var b = parseInt(ipArr[1]);
        var c = parseInt(ipArr[2]);
        var d = parseInt(ipArr[3]);
        var ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    },
    //#endregion

    //#region util
    isNull: function (obj) {
        return (obj === undefined || obj === null || obj === '') ? true : false;
    },
    isNumber: function (obj) {
        var reg = /^[0-9]*$/;
        return reg.test(obj);
    },
    copyObject: function (obj) {
        return JSON.parse(JSON.stringify(obj));
    },
    parameterTransfer: function (value, out) {
        if (this.isNull(value)) {
            return out;
        } else {
            return value;
        }
    },
    binaryDecode: function (number, p) {
        //从右向左数，第一个数是第一位，p传1，依次类推
        var position = Math.pow(2, p - 1);
        return (number & position) === position ? 1 : 0;
    },
    binaryDecode2: function (number, p) {
        //从右向左数，第一个数是第一位，p传1，依次类推
        var origin = 1;
        var position = origin << (p - 1);
        return (number & position) !== 0 ? 1 : 0;
    },
    fileToBase64: function (file) {
        //使用方法：this.fileToBase64(file).then(d=>{在这获取结果值});
        return new Promise((resolve, reject) => {
            var reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => {
                resolve(reader.result);
            }
        });
    },
    jsonFormatter: function (json) {
        var reg = null;
        var formatted = '';
        var pad = 0;
        var PADDING = '\t';
        var options = options || {};
        options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false;
        options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;
        if (typeof json !== 'string') {
            json = JSON.stringify(json);
        }
        json = JSON.parse(json);
        json = JSON.stringify(json);
        reg = /([\{\}])/g;
        json = json.replace(reg, '\r\n$1\r\n');
        reg = /([\[\]])/g;
        json = json.replace(reg, '\r\n$1\r\n');
        reg = /(\,)/g;
        json = json.replace(reg, '$1\r\n');
        reg = /(\r\n\r\n)/g;
        json = json.replace(reg, '\r\n');
        reg = /\r\n\,/g;
        json = json.replace(reg, ',');
        if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
            reg = /\:\r\n\{/g;
            json = json.replace(reg, ':{');
            reg = /\:\r\n\[/g;
            json = json.replace(reg, ':[');
        }
        if (options.spaceAfterColon) {
            reg = /\:/g;
            json = json.replace(reg, ': ');
        }
        json.split('\r\n').forEach((node) => {
            var i = 0;
            var indent = 0;
            var padding = '';
            if (node.match(/\{$/) || node.match(/\[$/)) {
                indent = 1;
            } else if (node.match(/\}/) || node.match(/\]/)) {
                if (pad !== 0) {
                    pad -= 1;
                }
            } else {
                indent = 0;
            }
            for (i = 0; i < pad; i++) {
                padding += PADDING;
            }
            formatted += padding + node + '\r\n';
            pad += indent;
        });
        return formatted;
    },
    groupBy: function (array, f) {
        //使用方法：结果值=this.groupBy(array,(x)=>{return [x.分组字段]})
        var groups = {};
        array.forEach((x) => {
            var group = JSON.stringify(f(x));
            groups[group] = groups[group] || [];
            groups[group].push(x);
        });
        return Object.keys(groups).map((group) => {
            return groups[group];
        });
    },
    //#endregion

    //#region 字符串
    startWith: function (origin, str) {
        var reg = new RegExp("^" + str);
        return reg.test(origin);
    },
    endWith: function (origin, str) {
        var reg = new RegExp(str + "$");
        return reg.test(origin);
    },
    contains: function (origin, str) {
        return origin.indexOf(str) >= 0;
    },
    //#endregion

    //#region 项目
    getSearchObject: function () {
        var obj = {
            loading: null,
            page: 1,
            limit: 10,
            string1: null,
            string2: null,
            number1: null,
            number2: null,
            datetime1: null,
            datetime2: null
        };
        return this.copyObject(obj);
    },
    //#endregion
};