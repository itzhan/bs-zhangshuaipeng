package com.rescue.petrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.petrescue.common.BusinessException;
import com.rescue.petrescue.common.PageResult;
import com.rescue.petrescue.entity.RescueRecord;
import com.rescue.petrescue.mapper.RescueRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RescueRecordService {
    private final RescueRecordMapper mapper;

    public PageResult<RescueRecord> list(Integer page, Integer size, Integer status, String keyword) {
        Page<RescueRecord> p = new Page<>(page, size);
        LambdaQueryWrapper<RescueRecord> w = new LambdaQueryWrapper<>();
        if (status != null) w.eq(RescueRecord::getStatus, status);
        if (StringUtils.hasText(keyword)) w.like(RescueRecord::getLocation, keyword);
        w.orderByDesc(RescueRecord::getCreatedAt);
        Page<RescueRecord> r = mapper.selectPage(p, w);
        return new PageResult<>(r.getRecords(), r.getTotal(), r.getCurrent(), r.getSize());
    }

    public RescueRecord getById(Long id) {
        RescueRecord r = mapper.selectById(id);
        if (r == null) throw new BusinessException(404, "救助记录不存在");
        return r;
    }

    public RescueRecord create(RescueRecord record) { mapper.insert(record); return record; }

    public RescueRecord update(Long id, RescueRecord update) {
        if (mapper.selectById(id) == null) throw new BusinessException(404, "救助记录不存在");
        update.setId(id); mapper.updateById(update); return mapper.selectById(id);
    }

    public void delete(Long id) { mapper.deleteById(id); }

    public void updateStatus(Long id, Integer status) {
        RescueRecord r = mapper.selectById(id);
        if (r == null) throw new BusinessException(404, "救助记录不存在");
        r.setStatus(status); mapper.updateById(r);
    }
}
