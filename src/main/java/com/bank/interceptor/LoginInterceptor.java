package com.bank.interceptor;

import com.bank.common.Constants;
import com.bank.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class LoginInterceptor implements HandlerInterceptor {
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<String> pageWhiteList = Arrays.asList(
            "/login",
            "/register"
    );
    private final List<String> apiWhiteList = Arrays.asList(
            "/api/login",
            "/api/register"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object h) throws Exception {
        String uri = req.getRequestURI();
        // 页面白名单直接放行
        if (pageWhiteList.contains(uri)) {
            return true;
        }
        // api白名单直接放行
        if (apiWhiteList.contains(uri)) {
            return true;
        }

        // 已登录，放行
        if (req.getSession().getAttribute(Constants.SESSION_USER) != null)
            return true;

        // 剩下其他/api接口返回401
        if (uri.startsWith("/api/")) {
            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter pw = resp.getWriter()) {
                pw.write(mapper.writeValueAsString(Result.unauthorized()));
            }
            return false;
        }
        // 普通页面跳转到登录页
        resp.sendRedirect(req.getContextPath() + "/login");
        return false;
    }
}
