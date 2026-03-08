package com.rescue.petrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.petrescue.common.BusinessException;
import com.rescue.petrescue.common.PageResult;
import com.rescue.petrescue.entity.*;
import com.rescue.petrescue.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final LostFoundMapper lostFoundMapper;
    private final RescueStationMapper stationMapper;
    private final SupplyMapper supplyMapper;
    private final VolunteerScheduleMapper volunteerMapper;
    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;
    private final CommentMapper commentMapper;
    private final FavoriteMapper favoriteMapper;
    private final NotificationMapper notificationMapper;
    private final SysDictMapper dictMapper;

    // === LostFound ===
    public PageResult<LostFound> listLostFound(Integer page, Integer size, Integer type, Integer status, String keyword) {
        Page<LostFound> p = new Page<>(page, size);
        LambdaQueryWrapper<LostFound> w = new LambdaQueryWrapper<>();
        if (type != null) w.eq(LostFound::getType, type);
        if (status != null) w.eq(LostFound::getStatus, status);
        if (StringUtils.hasText(keyword)) w.and(q -> q.like(LostFound::getPetName, keyword).or().like(LostFound::getDescription, keyword));
        w.orderByDesc(LostFound::getCreatedAt);
        Page<LostFound> r = lostFoundMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public LostFound getLostFoundById(Long id) { LostFound l = lostFoundMapper.selectById(id); if (l == null) throw new BusinessException(404, "记录不存在"); return l; }
    public LostFound createLostFound(LostFound lf) { lostFoundMapper.insert(lf); return lf; }
    public LostFound updateLostFound(Long id, LostFound lf) { lf.setId(id); lostFoundMapper.updateById(lf); return lostFoundMapper.selectById(id); }
    public void deleteLostFound(Long id) { lostFoundMapper.deleteById(id); }

    // === RescueStation ===
    public PageResult<RescueStation> listStations(Integer page, Integer size, String keyword) {
        Page<RescueStation> p = new Page<>(page, size);
        LambdaQueryWrapper<RescueStation> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) w.like(RescueStation::getName, keyword);
        w.orderByDesc(RescueStation::getCreatedAt);
        Page<RescueStation> r = stationMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public java.util.List<RescueStation> listAllStations() { return stationMapper.selectList(new LambdaQueryWrapper<RescueStation>().eq(RescueStation::getStatus, 1)); }
    public RescueStation getStationById(Long id) { RescueStation s = stationMapper.selectById(id); if (s == null) throw new BusinessException(404, "救助站不存在"); return s; }
    public RescueStation createStation(RescueStation s) { stationMapper.insert(s); return s; }
    public RescueStation updateStation(Long id, RescueStation s) { s.setId(id); stationMapper.updateById(s); return stationMapper.selectById(id); }
    public void deleteStation(Long id) { stationMapper.deleteById(id); }

    // === Supply ===
    public PageResult<Supply> listSupplies(Integer page, Integer size, Long stationId, String category) {
        Page<Supply> p = new Page<>(page, size);
        LambdaQueryWrapper<Supply> w = new LambdaQueryWrapper<>();
        if (stationId != null) w.eq(Supply::getStationId, stationId);
        if (StringUtils.hasText(category)) w.eq(Supply::getCategory, category);
        w.orderByAsc(Supply::getCategory);
        Page<Supply> r = supplyMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public Supply createSupply(Supply s) { supplyMapper.insert(s); return s; }
    public Supply updateSupply(Long id, Supply s) { s.setId(id); supplyMapper.updateById(s); return supplyMapper.selectById(id); }
    public void deleteSupply(Long id) { supplyMapper.deleteById(id); }
    public java.util.List<Supply> getLowStockSupplies() {
        return supplyMapper.selectList(new LambdaQueryWrapper<Supply>().apply("quantity <= threshold"));
    }

    // === Volunteer ===
    public PageResult<VolunteerSchedule> listVolunteers(Integer page, Integer size, Long stationId, String date) {
        Page<VolunteerSchedule> p = new Page<>(page, size);
        LambdaQueryWrapper<VolunteerSchedule> w = new LambdaQueryWrapper<>();
        if (stationId != null) w.eq(VolunteerSchedule::getStationId, stationId);
        if (StringUtils.hasText(date)) w.eq(VolunteerSchedule::getScheduleDate, date);
        w.orderByDesc(VolunteerSchedule::getScheduleDate);
        Page<VolunteerSchedule> r = volunteerMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public VolunteerSchedule createVolunteer(VolunteerSchedule v) { volunteerMapper.insert(v); return v; }
    public VolunteerSchedule updateVolunteer(Long id, VolunteerSchedule v) { v.setId(id); volunteerMapper.updateById(v); return volunteerMapper.selectById(id); }
    public void deleteVolunteer(Long id) { volunteerMapper.deleteById(id); }

    // === Activity ===
    public PageResult<Activity> listActivities(Integer page, Integer size, Integer status) {
        Page<Activity> p = new Page<>(page, size);
        LambdaQueryWrapper<Activity> w = new LambdaQueryWrapper<>();
        if (status != null) w.eq(Activity::getStatus, status);
        w.orderByDesc(Activity::getStartTime);
        Page<Activity> r = activityMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public Activity getActivityById(Long id) { Activity a = activityMapper.selectById(id); if (a == null) throw new BusinessException(404, "活动不存在"); return a; }
    public Activity createActivity(Activity a) { activityMapper.insert(a); return a; }
    public Activity updateActivity(Long id, Activity a) { a.setId(id); activityMapper.updateById(a); return activityMapper.selectById(id); }
    public void deleteActivity(Long id) { activityMapper.deleteById(id); }

    // === Activity Registration ===
    public void registerActivity(Long activityId, Long userId) {
        Activity a = activityMapper.selectById(activityId);
        if (a == null) throw new BusinessException(404, "活动不存在");
        if (a.getMaxParticipants() > 0 && a.getCurrentParticipants() >= a.getMaxParticipants())
            throw new BusinessException(400, "活动报名已满");
        Long c = registrationMapper.selectCount(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId).eq(ActivityRegistration::getUserId, userId));
        if (c > 0) throw new BusinessException(400, "您已报名该活动");
        ActivityRegistration reg = new ActivityRegistration();
        reg.setActivityId(activityId); reg.setUserId(userId); reg.setStatus(0);
        registrationMapper.insert(reg);
        a.setCurrentParticipants(a.getCurrentParticipants() + 1);
        activityMapper.updateById(a);
    }
    public void cancelRegistration(Long activityId, Long userId) {
        ActivityRegistration reg = registrationMapper.selectOne(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId).eq(ActivityRegistration::getUserId, userId));
        if (reg == null) throw new BusinessException(404, "未找到报名记录");
        reg.setStatus(2); registrationMapper.updateById(reg);
        Activity a = activityMapper.selectById(activityId);
        if (a != null && a.getCurrentParticipants() > 0) {
            a.setCurrentParticipants(a.getCurrentParticipants() - 1); activityMapper.updateById(a);
        }
    }
    public PageResult<ActivityRegistration> listRegistrations(Integer page, Integer size, Long activityId, Long userId) {
        Page<ActivityRegistration> p = new Page<>(page, size);
        LambdaQueryWrapper<ActivityRegistration> w = new LambdaQueryWrapper<>();
        if (activityId != null) w.eq(ActivityRegistration::getActivityId, activityId);
        if (userId != null) w.eq(ActivityRegistration::getUserId, userId);
        Page<ActivityRegistration> r = registrationMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }

    // === Comment ===
    public PageResult<Comment> listComments(Integer page, Integer size, String targetType, Long targetId) {
        Page<Comment> p = new Page<>(page, size);
        LambdaQueryWrapper<Comment> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(targetType)) w.eq(Comment::getTargetType, targetType);
        if (targetId != null) w.eq(Comment::getTargetId, targetId);
        w.orderByDesc(Comment::getCreatedAt);
        Page<Comment> r = commentMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public Comment createComment(Comment c) { commentMapper.insert(c); return c; }
    public void deleteComment(Long id, Long userId) {
        Comment c = commentMapper.selectById(id);
        if (c != null && c.getUserId().equals(userId)) commentMapper.deleteById(id);
        else throw new BusinessException(403, "无权删除");
    }
    public void adminDeleteComment(Long id) { commentMapper.deleteById(id); }

    // === Favorite ===
    public void addFavorite(Long userId, Long petId) {
        Long c = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId));
        if (c > 0) throw new BusinessException(400, "已收藏");
        Favorite f = new Favorite(); f.setUserId(userId); f.setPetId(petId);
        favoriteMapper.insert(f);
    }
    public void removeFavorite(Long userId, Long petId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId));
    }
    public PageResult<Favorite> listFavorites(Integer page, Integer size, Long userId) {
        Page<Favorite> p = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> w = new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId);
        Page<Favorite> r = favoriteMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public boolean isFavorite(Long userId, Long petId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId)) > 0;
    }

    // === Notification ===
    public PageResult<Notification> listNotifications(Integer page, Integer size, Long userId, Integer isRead) {
        Page<Notification> p = new Page<>(page, size);
        LambdaQueryWrapper<Notification> w = new LambdaQueryWrapper<>();
        w.eq(Notification::getUserId, userId);
        if (isRead != null) w.eq(Notification::getIsRead, isRead);
        w.orderByDesc(Notification::getCreatedAt);
        Page<Notification> r = notificationMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public void markRead(Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n != null) { n.setIsRead(1); notificationMapper.updateById(n); }
    }
    public void markAllRead(Long userId) {
        notificationMapper.selectList(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0))
                .forEach(n -> { n.setIsRead(1); notificationMapper.updateById(n); });
    }
    public long unreadCount(Long userId) {
        return notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0));
    }

    // === Dict ===
    public java.util.List<SysDict> listDictByType(String type) {
        return dictMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getType, type).eq(SysDict::getStatus, 1).orderByAsc(SysDict::getSort));
    }
    public PageResult<SysDict> listDicts(Integer page, Integer size, String type) {
        Page<SysDict> p = new Page<>(page, size);
        LambdaQueryWrapper<SysDict> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) w.eq(SysDict::getType, type);
        w.orderByAsc(SysDict::getType).orderByAsc(SysDict::getSort);
        Page<SysDict> r = dictMapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }
    public SysDict createDict(SysDict d) { dictMapper.insert(d); return d; }
    public SysDict updateDict(Long id, SysDict d) { d.setId(id); dictMapper.updateById(d); return dictMapper.selectById(id); }
    public void deleteDict(Long id) { dictMapper.deleteById(id); }
}
