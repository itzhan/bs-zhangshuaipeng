package com.rescue.petrescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.petrescue.common.BusinessException;
import com.rescue.petrescue.common.PageResult;
import com.rescue.petrescue.dto.LoginDTO;
import com.rescue.petrescue.dto.RegisterDTO;
import com.rescue.petrescue.entity.User;
import com.rescue.petrescue.mapper.UserMapper;
import com.rescue.petrescue.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", toSafeUser(user));
        return result;
    }

    public User register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
        return toSafeUser(user);
    }

    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        return toSafeUser(user);
    }

    public User updateProfile(Long userId, User update) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (update.getNickname() != null) user.setNickname(update.getNickname());
        if (update.getPhone() != null) user.setPhone(update.getPhone());
        if (update.getEmail() != null) user.setEmail(update.getEmail());
        if (update.getAvatar() != null) user.setAvatar(update.getAvatar());
        if (update.getGender() != null) user.setGender(update.getGender());
        userMapper.updateById(user);
        return toSafeUser(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    public PageResult<User> list(Integer page, Integer size, String keyword, String role) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    public User adminUpdate(Long id, User update) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (update.getNickname() != null) user.setNickname(update.getNickname());
        if (update.getPhone() != null) user.setPhone(update.getPhone());
        if (update.getEmail() != null) user.setEmail(update.getEmail());
        if (update.getGender() != null) user.setGender(update.getGender());
        if (update.getRole() != null) user.setRole(update.getRole());
        if (update.getStatus() != null) user.setStatus(update.getStatus());
        if (StringUtils.hasText(update.getPassword())) {
            user.setPassword(passwordEncoder.encode(update.getPassword()));
        }
        userMapper.updateById(user);
        return toSafeUser(user);
    }

    private User toSafeUser(User user) {
        user.setPassword(null);
        return user;
    }
}
