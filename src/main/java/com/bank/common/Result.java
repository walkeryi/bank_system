package com.bank.common;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private int code; private String msg; private T data;
    public Result() {}
    public Result(int code, String msg, T data) { this.code = code; this.msg = msg; this.data = data; }
    public static <T> Result<T> ok(T d) { return new Result<>(200, "操作成功", d); }
    public static <T> Result<T> ok() { return new Result<>(200, "操作成功", null); }
    public static <T> Result<T> fail(String m) { return new Result<>(400, m, null); }
    public static <T> Result<T> unauthorized() { return new Result<>(401, "未登录或登录已过期", null); }
    public int getCode() { return code; } public void setCode(int c) { this.code = c; }
    public String getMsg() { return msg; } public void setMsg(String m) { this.msg = m; }
    public T getData() { return data; } public void setData(T d) { this.data = d; }
}
