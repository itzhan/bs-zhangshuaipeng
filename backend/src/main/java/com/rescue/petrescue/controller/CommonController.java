package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.entity.*;
import com.rescue.petrescue.service.CommonService;
import com.rescue.petrescue.service.RescueRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;
    private final RescueRecordService rescueRecordService;

    // ==================== 救助记录 ====================
    @GetMapping("/api/rescues")
    public Result<?> listRescues(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String keyword) {
        return Result.success(rescueRecordService.list(page, size, status, keyword));
    }

    @GetMapping("/api/rescues/{id}")
    public Result<?> getRescue(@PathVariable Long id) {
        return Result.success(rescueRecordService.getById(id));
    }

    @PostMapping("/api/rescues")
    public Result<?> createRescue(Authentication auth, @RequestBody RescueRecord record) {
        Long userId = (Long) auth.getPrincipal();
        record.setReporterId(userId);
        return Result.success(rescueRecordService.create(record));
    }

    @PutMapping("/api/rescues/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateRescue(@PathVariable Long id, @RequestBody RescueRecord record) {
        return Result.success(rescueRecordService.update(id, record));
    }

    @PutMapping("/api/rescues/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateRescueStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        rescueRecordService.updateStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }

    @DeleteMapping("/api/rescues/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteRescue(@PathVariable Long id) {
        rescueRecordService.delete(id);
        return Result.success("删除成功");
    }

    // ==================== 失宠招领 ====================
    @GetMapping("/api/lost-found")
    public Result<?> listLostFound(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(required = false) Integer type,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String keyword) {
        return Result.success(commonService.listLostFound(page, size, type, status, keyword));
    }

    @GetMapping("/api/lost-found/{id}")
    public Result<?> getLostFound(@PathVariable Long id) {
        return Result.success(commonService.getLostFoundById(id));
    }

    @PostMapping("/api/lost-found")
    public Result<?> createLostFound(Authentication auth, @RequestBody LostFound lf) {
        Long userId = (Long) auth.getPrincipal();
        lf.setUserId(userId);
        return Result.success(commonService.createLostFound(lf));
    }

    @PutMapping("/api/lost-found/{id}")
    public Result<?> updateLostFound(@PathVariable Long id, @RequestBody LostFound lf) {
        return Result.success(commonService.updateLostFound(id, lf));
    }

    @DeleteMapping("/api/lost-found/{id}")
    public Result<?> deleteLostFound(@PathVariable Long id) {
        commonService.deleteLostFound(id);
        return Result.success("删除成功");
    }

    // ==================== 救助站 ====================
    @GetMapping("/api/stations")
    public Result<?> listStations(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String keyword) {
        return Result.success(commonService.listStations(page, size, keyword));
    }

    @GetMapping("/api/stations/all")
    public Result<?> listAllStations() {
        return Result.success(commonService.listAllStations());
    }

    @GetMapping("/api/stations/{id}")
    public Result<?> getStation(@PathVariable Long id) {
        return Result.success(commonService.getStationById(id));
    }

    @PostMapping("/api/stations")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createStation(@RequestBody RescueStation station) {
        return Result.success(commonService.createStation(station));
    }

    @PutMapping("/api/stations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStation(@PathVariable Long id, @RequestBody RescueStation station) {
        return Result.success(commonService.updateStation(id, station));
    }

    @DeleteMapping("/api/stations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteStation(@PathVariable Long id) {
        commonService.deleteStation(id);
        return Result.success("删除成功");
    }

    // ==================== 物资管理 ====================
    @GetMapping("/api/supplies")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> listSupplies(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) Long stationId,
                                   @RequestParam(required = false) String category) {
        return Result.success(commonService.listSupplies(page, size, stationId, category));
    }

    @PostMapping("/api/supplies")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> createSupply(@RequestBody Supply supply) {
        return Result.success(commonService.createSupply(supply));
    }

    @PutMapping("/api/supplies/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateSupply(@PathVariable Long id, @RequestBody Supply supply) {
        return Result.success(commonService.updateSupply(id, supply));
    }

    @DeleteMapping("/api/supplies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteSupply(@PathVariable Long id) {
        commonService.deleteSupply(id);
        return Result.success("删除成功");
    }

    @GetMapping("/api/supplies/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> lowStock() {
        return Result.success(commonService.getLowStockSupplies());
    }

    // ==================== 志愿者排班 ====================
    @GetMapping("/api/volunteers")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> listVolunteers(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Long stationId,
                                     @RequestParam(required = false) String date) {
        return Result.success(commonService.listVolunteers(page, size, stationId, date));
    }

    @PostMapping("/api/volunteers")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> createVolunteer(@RequestBody VolunteerSchedule v) {
        return Result.success(commonService.createVolunteer(v));
    }

    @PutMapping("/api/volunteers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateVolunteer(@PathVariable Long id, @RequestBody VolunteerSchedule v) {
        return Result.success(commonService.updateVolunteer(id, v));
    }

    @DeleteMapping("/api/volunteers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteVolunteer(@PathVariable Long id) {
        commonService.deleteVolunteer(id);
        return Result.success("删除成功");
    }

    // ==================== 活动 ====================
    @GetMapping("/api/activities")
    public Result<?> listActivities(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Integer status) {
        return Result.success(commonService.listActivities(page, size, status));
    }

    @GetMapping("/api/activities/{id}")
    public Result<?> getActivity(@PathVariable Long id) {
        return Result.success(commonService.getActivityById(id));
    }

    @PostMapping("/api/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createActivity(@RequestBody Activity activity) {
        return Result.success(commonService.createActivity(activity));
    }

    @PutMapping("/api/activities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        return Result.success(commonService.updateActivity(id, activity));
    }

    @DeleteMapping("/api/activities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteActivity(@PathVariable Long id) {
        commonService.deleteActivity(id);
        return Result.success("删除成功");
    }

    @PostMapping("/api/activities/{id}/register")
    public Result<?> registerActivity(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.registerActivity(id, userId);
        return Result.success("报名成功");
    }

    @PostMapping("/api/activities/{id}/cancel")
    public Result<?> cancelRegistration(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.cancelRegistration(id, userId);
        return Result.success("取消成功");
    }

    @GetMapping("/api/activities/{id}/registrations")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> listRegistrations(@PathVariable Long id,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commonService.listRegistrations(page, size, id, null));
    }

    @GetMapping("/api/activities/my")
    public Result<?> myRegistrations(Authentication auth,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commonService.listRegistrations(page, size, null, userId));
    }

    // ==================== 评论 ====================
    @GetMapping("/api/comments")
    public Result<?> listComments(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String targetType,
                                   @RequestParam(required = false) Long targetId) {
        return Result.success(commonService.listComments(page, size, targetType, targetId));
    }

    @PostMapping("/api/comments")
    public Result<?> createComment(Authentication auth, @RequestBody Comment comment) {
        Long userId = (Long) auth.getPrincipal();
        comment.setUserId(userId);
        return Result.success(commonService.createComment(comment));
    }

    @DeleteMapping("/api/comments/{id}")
    public Result<?> deleteComment(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.deleteComment(id, userId);
        return Result.success("删除成功");
    }

    @DeleteMapping("/api/comments/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminDeleteComment(@PathVariable Long id) {
        commonService.adminDeleteComment(id);
        return Result.success("删除成功");
    }

    // ==================== 收藏 ====================
    @PostMapping("/api/favorites/{petId}")
    public Result<?> addFavorite(@PathVariable Long petId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.addFavorite(userId, petId);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/api/favorites/{petId}")
    public Result<?> removeFavorite(@PathVariable Long petId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.removeFavorite(userId, petId);
        return Result.success("取消收藏");
    }

    @GetMapping("/api/favorites")
    public Result<?> listFavorites(Authentication auth,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commonService.listFavorites(page, size, userId));
    }

    @GetMapping("/api/favorites/check/{petId}")
    public Result<?> checkFavorite(@PathVariable Long petId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commonService.isFavorite(userId, petId));
    }

    // ==================== 通知 ====================
    @GetMapping("/api/notifications")
    public Result<?> listNotifications(Authentication auth,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) Integer isRead) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commonService.listNotifications(page, size, userId, isRead));
    }

    @PutMapping("/api/notifications/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        commonService.markRead(id);
        return Result.success("已读");
    }

    @PutMapping("/api/notifications/read-all")
    public Result<?> markAllRead(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commonService.markAllRead(userId);
        return Result.success("全部已读");
    }

    @GetMapping("/api/notifications/unread-count")
    public Result<?> unreadCount(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commonService.unreadCount(userId));
    }

    // ==================== 数据字典 ====================
    @GetMapping("/api/dict/{type}")
    public Result<?> listDictByType(@PathVariable String type) {
        return Result.success(commonService.listDictByType(type));
    }

    @GetMapping("/api/dict")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> listDicts(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestParam(required = false) String type) {
        return Result.success(commonService.listDicts(page, size, type));
    }

    @PostMapping("/api/dict")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createDict(@RequestBody SysDict dict) {
        return Result.success(commonService.createDict(dict));
    }

    @PutMapping("/api/dict/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateDict(@PathVariable Long id, @RequestBody SysDict dict) {
        return Result.success(commonService.updateDict(id, dict));
    }

    @DeleteMapping("/api/dict/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteDict(@PathVariable Long id) {
        commonService.deleteDict(id);
        return Result.success("删除成功");
    }
}
