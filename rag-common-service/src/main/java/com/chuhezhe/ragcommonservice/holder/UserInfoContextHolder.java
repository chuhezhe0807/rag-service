package com.chuhezhe.ragcommonservice.holder;

import com.chuhezhe.ragcommonservice.vo.UserVO;
import org.springframework.stereotype.Component;

/**
 * 用户信息上下文holder，存储当前请求的用户信息
 */
@Component
public class UserInfoContextHolder {

    private static final ThreadLocal<UserVO> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前请求的用户信息
     * @param userVO 用户信息
     */
    public static void setUserInfo(UserVO userVO) {
        USER_INFO_THREAD_LOCAL.set(userVO);
    }

    /**
     * 获取当前请求的用户信息
     */
    public static UserVO getUserInfo() {
        return USER_INFO_THREAD_LOCAL.get();
    }

    /**
     * 清除当前请求的用户信息
     */
     public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}
