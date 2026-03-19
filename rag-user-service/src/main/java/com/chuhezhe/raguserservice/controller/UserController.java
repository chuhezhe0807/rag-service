package com.chuhezhe.raguserservice.controller;

import com.chuhezhe.common.constants.GConstants;
import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.annotation.AnonymousAccess;
import com.chuhezhe.ragcommonservice.vo.UserVO;
import com.chuhezhe.raguserservice.dto.UserLoginDTO;
import com.chuhezhe.raguserservice.dto.UserRegisterDTO;
import com.chuhezhe.raguserservice.service.IUserService;
import com.chuhezhe.raguserservice.vo.UserLoginVO;
import com.chuhezhe.raguserservice.vo.UserRegisterVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/ai/auth")
@RequiredArgsConstructor
public class UserController {

    public final IUserService userService;

    @AnonymousAccess
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Result<UserLoginVO> login(
            @RequestParam("username")
            @NotBlank(message = "{validation.adminUser.username.notNull}")
            @Size(min = 3, max = 50, message = "{validation.adminUser.username.size}")
            String username,

            @RequestParam("password")
            @NotBlank(message = "{validation.adminUser.password.notBlank}")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
                    message = "{validation.adminUser.password.pattern}"
            )
            String password
    ) {
        UserLoginDTO loginDTO = new UserLoginDTO(username,  password);
        return userService.login(loginDTO);
    }

    @AnonymousAccess
    @PostMapping("/register")
    public Result<UserRegisterVO> register(@Valid @RequestBody UserRegisterDTO registerRequest) {
        return userService.register(registerRequest);
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@RequestHeader(GConstants.JWT_TOKEN_HEADER) String token) {
        return userService.getUserInfo(token);
    }

    /**
     * 根据token获取用户信息，直接传token参数获取用户信息
     */
    @GetMapping("/user")
    public Result<UserVO> getUserInfoWithToken(String token) {
        return userService.getUserInfo(token);
    }
}
