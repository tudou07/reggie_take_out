package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter("/*")
public class LoginCheckFilter2 implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //此处需要用到直接返回string的方法
        String requestURL = request.getRequestURI();
//        log.info("过滤器启动过滤。。。拦截到请求：{}",requestURL);
        //定义不需要处理的请求路径
        String[] paths = new String[]{"/employee/login","employee/logout","/backend/**","/front/**","/user/login"};
        boolean check = check(paths, requestURL);
//        log.info("check={}",check);
        if (check){
            log.info("本次请求不需要拦截:{}",requestURL);
            filterChain.doFilter(request,response);
            return;
        }
        //判断账号密码方式登陆
        Object id = request.getSession().getAttribute("employee");
        if (id != null){
            log.info("员工已登陆，id为{}", id);
            //设置当前线程局部变量id的值，公共字段自动填充用
            BaseContext.setCurrentId((Long) id);
            filterChain.doFilter(request,response);
            return;
        }

        //判断手机号验证码登陆方式
        Object userId = request.getSession().getAttribute("user");
        if (userId != null){
            log.info("用户已登陆，id为{}", userId);
            //设置当前线程局部变量id的值，公共字段自动填充用
            BaseContext.setCurrentId((Long) userId);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("未登陆，请求为{}",requestURL);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] paths,String requestURL){
        for (String path : paths){
            boolean match = PATH_MATCHER.match(path, requestURL);
            if (match)
                return true;
        }
        return false;
    }
}
