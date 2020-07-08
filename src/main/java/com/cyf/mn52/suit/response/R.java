package com.cyf.mn52.suit.response;

public class R {

    public static Result success(String message) {
        Result result = new Result();
        result.success = true;
        result.message = message;
        return result;
    }

    public static Result success(String message, Object data) {
        Result result = new Result();
        result.success = true;
        result.message = message;
        result.data = data;
        return result;
    }

    public static Result success(String message, int count, Object data) {
        Result result = new Result();
        result.success = true;
        result.message = message;
        result.count = count;
        result.data = data;
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        return result;
    }

    public static Result error(String message, Object data) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        result.data = data;
        return result;
    }

    public static Result exception(String message, Object data) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        result.data = data;
        return result;
    }
}
