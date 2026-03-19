package com.chuhezhe.ragcommonservice.interceptor;

import com.chuhezhe.common.constants.ErrorConstants;
import com.chuhezhe.common.constants.GConstants;
import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.annotation.AnonymousAccess;
import com.chuhezhe.ragcommonservice.feign.UserServiceClient;
import com.chuhezhe.ragcommonservice.holder.UserInfoContextHolder;
import com.chuhezhe.ragcommonservice.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    // 用户服务
    private UserServiceClient userServiceClient;

    @Autowired
    @Lazy // 延迟加载，避免循环依赖 当AuthInterceptor被创建时，UserServiceClient不会立即被初始化，从而打破循环依赖关系
    public void setUserServiceClient(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.判断是否是controller的方法，并且标记了@AnonymousAccess
        if(!(handler instanceof HandlerMethod)) {
            return true; // 不是controller的方法(如静态资源)，直接放行
        }

        AnonymousAccess methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(AnonymousAccess.class);
        if(methodAnnotation != null) {
            return true; // 标记了@AnonymousAccess，直接放行
        }

        AnonymousAccess clazzAnnotation = ((HandlerMethod) handler).getBeanType().getAnnotation(AnonymousAccess.class);
        if(clazzAnnotation != null) {
            return true; // 类标记了@AnonymousAccess，直接放行
        }

        // 2.从请求头获取token
        String token = request.getHeader(GConstants.JWT_TOKEN_HEADER);

        if(token == null || token.isEmpty()) {
            // token为空，返回未授权错误
            response.setContentType("application/json;charset=UTF-8");
            Result<Object> error = Result.error(ErrorConstants.UNAUTHORIZED);
            response.getWriter().write(error.toString());

            return false;
        }

        // 调用用户服务获取用户信息
        Result<UserVO> userResult = userServiceClient.getUserInfo(token);

        if (!userResult.isSuccess()) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Object> error = Result.error(ErrorConstants.UNAUTHORIZED);
            response.getWriter().write(error.toString());

            return false;
        }

        // 3.将用户信息设置到ThreadLocal
        UserInfoContextHolder.setUserInfo(userResult.getData());

        return true; // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserInfoContextHolder.clear();
    }
}
