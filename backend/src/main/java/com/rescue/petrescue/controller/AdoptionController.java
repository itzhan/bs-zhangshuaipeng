package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.entity.AdoptionApplication;
import com.rescue.petrescue.service.AdoptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) Long petId) {
        return Result.success(adoptionService.list(page, size, status, userId, petId));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(adoptionService.getById(id));
    }

    @PostMapping
    public Result<?> apply(Authentication auth, @RequestBody AdoptionApplication app) {
        Long userId = (Long) auth.getPrincipal();
        app.setUserId(userId);
        return Result.success(adoptionService.apply(app));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> review(@PathVariable Long id, Authentication auth,
                            @RequestBody Map<String, Object> body) {
        Long reviewerId = (Long) auth.getPrincipal();
        Integer status = (Integer) body.get("status");
        String reviewNote = (String) body.get("reviewNote");
        return Result.success(adoptionService.review(id, status, reviewNote, reviewerId));
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        adoptionService.cancel(id, userId);
        return Result.success("取消成功");
    }

    @GetMapping("/my")
    public Result<?> myApplications(Authentication auth,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(adoptionService.list(page, size, null, userId, null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        adoptionService.delete(id);
        return Result.success("删除成功");
    }
}
