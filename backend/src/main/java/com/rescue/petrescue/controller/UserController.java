package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.entity.User;
import com.rescue.petrescue.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String role) {
        return Result.success(userService.list(page, size, keyword, role));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(Authentication auth, @RequestBody User update) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(userService.updateProfile(userId, update));
    }

    @PutMapping("/password")
    public Result<?> changePassword(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.success("密码修改成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminUpdate(@PathVariable Long id, @RequestBody User update) {
        return Result.success(userService.adminUpdate(id, update));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }
}
