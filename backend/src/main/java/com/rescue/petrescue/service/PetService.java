package com.rescue.petrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.petrescue.common.BusinessException;
import com.rescue.petrescue.common.PageResult;
import com.rescue.petrescue.entity.Pet;
import com.rescue.petrescue.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetMapper petMapper;

    public PageResult<Pet> list(Integer page, Integer size, String species, Integer status,
                                Integer gender, String keyword) {
        Page<Pet> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(species)) wrapper.eq(Pet::getSpecies, species);
        if (status != null) wrapper.eq(Pet::getStatus, status);
        if (gender != null) wrapper.eq(Pet::getGender, gender);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Pet::getName, keyword)
                    .or().like(Pet::getBreed, keyword)
                    .or().like(Pet::getDescription, keyword));
        }
        wrapper.orderByDesc(Pet::getCreatedAt);
        Page<Pet> result = petMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Pet getById(Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) throw new BusinessException(404, "宠物不存在");
        return pet;
    }

    public Pet create(Pet pet) {
        petMapper.insert(pet);
        return pet;
    }

    public Pet update(Long id, Pet update) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) throw new BusinessException(404, "宠物不存在");
        update.setId(id);
        petMapper.updateById(update);
        return petMapper.selectById(id);
    }

    public void delete(Long id) {
        petMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) throw new BusinessException(404, "宠物不存在");
        pet.setStatus(status);
        petMapper.updateById(pet);
    }

    public long countByStatus(Integer status) {
        return petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, status));
    }

    public long countBySpecies(String species) {
        return petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getSpecies, species));
    }

    public long totalCount() {
        return petMapper.selectCount(null);
    }
}
