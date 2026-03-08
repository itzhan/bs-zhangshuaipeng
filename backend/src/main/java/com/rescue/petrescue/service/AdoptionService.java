package com.rescue.petrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.petrescue.common.BusinessException;
import com.rescue.petrescue.common.PageResult;
import com.rescue.petrescue.entity.AdoptionApplication;
import com.rescue.petrescue.entity.Notification;
import com.rescue.petrescue.entity.Pet;
import com.rescue.petrescue.mapper.AdoptionApplicationMapper;
import com.rescue.petrescue.mapper.NotificationMapper;
import com.rescue.petrescue.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionApplicationMapper adoptionMapper;
    private final PetMapper petMapper;
    private final NotificationMapper notificationMapper;

    public AdoptionApplication apply(AdoptionApplication application) {
        // 检查宠物是否存在且可领养
        Pet pet = petMapper.selectById(application.getPetId());
        if (pet == null) throw new BusinessException(404, "宠物不存在");
        if (pet.getStatus() != 2) throw new BusinessException(400, "该宠物当前不可领养");

        // 检查是否已申请
        Long count = adoptionMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getPetId, application.getPetId())
                .eq(AdoptionApplication::getUserId, application.getUserId())
                .in(AdoptionApplication::getStatus, 0, 1));  // 待审核或已通过
        if (count > 0) throw new BusinessException(400, "您已提交过该宠物的领养申请");

        application.setStatus(0);
        adoptionMapper.insert(application);
        return application;
    }

    @Transactional
    public AdoptionApplication review(Long id, Integer status, String reviewNote, Long reviewerId) {
        AdoptionApplication app = adoptionMapper.selectById(id);
        if (app == null) throw new BusinessException(404, "申请不存在");
        if (app.getStatus() != 0) throw new BusinessException(400, "该申请已处理");

        app.setStatus(status);
        app.setReviewNote(reviewNote);
        app.setReviewerId(reviewerId);
        app.setReviewTime(LocalDateTime.now());
        adoptionMapper.updateById(app);

        // 通过时更新宠物状态
        if (status == 1) {
            Pet pet = petMapper.selectById(app.getPetId());
            if (pet != null) {
                pet.setStatus(3); // 已领养
                petMapper.updateById(pet);
            }
        }

        // 发送通知
        Notification notification = new Notification();
        notification.setUserId(app.getUserId());
        notification.setType("ADOPTION");
        if (status == 1) {
            Pet pet = petMapper.selectById(app.getPetId());
            notification.setTitle("领养申请已通过");
            notification.setContent("恭喜您！您对\"" + (pet != null ? pet.getName() : "") + "\"的领养申请已通过审核。");
        } else {
            Pet pet = petMapper.selectById(app.getPetId());
            notification.setTitle("领养申请被拒绝");
            notification.setContent("您对\"" + (pet != null ? pet.getName() : "") + "\"的领养申请未通过。原因：" + reviewNote);
        }
        notification.setIsRead(0);
        notificationMapper.insert(notification);

        return app;
    }

    public PageResult<AdoptionApplication> list(Integer page, Integer size, Integer status,
                                                 Long userId, Long petId) {
        Page<AdoptionApplication> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(AdoptionApplication::getStatus, status);
        if (userId != null) wrapper.eq(AdoptionApplication::getUserId, userId);
        if (petId != null) wrapper.eq(AdoptionApplication::getPetId, petId);
        wrapper.orderByDesc(AdoptionApplication::getCreatedAt);
        Page<AdoptionApplication> result = adoptionMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public AdoptionApplication getById(Long id) {
        AdoptionApplication app = adoptionMapper.selectById(id);
        if (app == null) throw new BusinessException(404, "申请不存在");
        return app;
    }

    public void cancel(Long id, Long userId) {
        AdoptionApplication app = adoptionMapper.selectById(id);
        if (app == null) throw new BusinessException(404, "申请不存在");
        if (!app.getUserId().equals(userId)) throw new BusinessException(403, "无权操作");
        if (app.getStatus() != 0) throw new BusinessException(400, "只能取消待审核的申请");
        app.setStatus(3);
        adoptionMapper.updateById(app);
    }

    public void delete(Long id) {
        adoptionMapper.deleteById(id);
    }
}
