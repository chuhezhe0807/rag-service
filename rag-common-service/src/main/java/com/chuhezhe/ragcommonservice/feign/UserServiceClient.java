package com.chuhezhe.ragcommonservice.feign;

import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "rag-user-service")
public interface UserServiceClient {

    @GetMapping("api/ai/auth/user")
    Result<UserVO> getUserInfo(String token);
}
