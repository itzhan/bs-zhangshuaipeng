package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.dto.LoginDTO;
import com.rescue.petrescue.dto.RegisterDTO;
import com.rescue.petrescue.service.UserService;
import com.rescue.petrescue.util.FileUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final FileUtil fileUtil;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @GetMapping("/info")
    public Result<?> info(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(userService.getById(userId));
    }

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(defaultValue = "common") String dir) {
        return Result.success(fileUtil.upload(file, dir));
    }
}
