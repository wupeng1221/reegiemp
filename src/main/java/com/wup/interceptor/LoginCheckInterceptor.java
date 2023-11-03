package com.wup.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.wup.common.Result;
import com.wup.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("收到请求并拦截 ({}){}", request.getMethod(), request.getRequestURI());

        // 处理请求的url，判断是否是登录请求,是登录请求直接放行
        String url = request.getRequestURI();
        if (url.contains("login")) {
            log.info("登录操作，放行...");
            return true;
        }
        // 根据session获取用户登录状态
        String token = (String) request.getSession().getAttribute("jwt");
        if (!StringUtils.hasLength(token)) {
            // session为空，用户未登录
            log.info("session不存在，未登录...");
            Result<Object> respResult = Result.error("NOTLOGIN");
            String respJSON = JSONObject.toJSONString(respResult);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(respJSON);
            return false;
        }
        // session不为空，解析session中包含的令牌是否合法，
        // 由于服务器只会在成功登录时生成jwt并封装在session中
        // 因此不用校验是否存在jwt，只需校验jwt是否合法
        try {
            JwtUtils.parseJWT(token);
        } catch (Exception e) {
            log.info("token解析失败，令牌不合法...");
            Result<Object> respResult = Result.error("NOTLOGIN");
            String respJSON = JSONObject.toJSONString(respResult);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(respJSON);
            return false;
        }
        log.info("session存在且token合法, 放行请求 {}", request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("放行请求 {}", request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        return;
    }
}
