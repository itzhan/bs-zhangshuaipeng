package com.rescue.petrescue.service;

import com.rescue.petrescue.entity.*;
import com.rescue.petrescue.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PetMapper petMapper;
    private final RescueRecordMapper rescueMapper;
    private final AdoptionApplicationMapper adoptionMapper;
    private final UserMapper userMapper;
    private final RescueStationMapper stationMapper;
    private final ActivityMapper activityMapper;

    public Map<String, Object> getOverview() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalPets", petMapper.selectCount(null));
        data.put("waitingAdoption", petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 2)));
        data.put("adopted", petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 3)));
        data.put("rescuing", petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, 1)));
        data.put("totalUsers", userMapper.selectCount(null));
        data.put("totalRescues", rescueMapper.selectCount(null));
        data.put("pendingApplications", adoptionMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, 0)));
        data.put("totalStations", stationMapper.selectCount(new LambdaQueryWrapper<RescueStation>().eq(RescueStation::getStatus, 1)));
        return data;
    }

    public List<Map<String, Object>> getPetSpeciesStats() {
        List<Pet> pets = petMapper.selectList(null);
        return pets.stream()
                .collect(Collectors.groupingBy(Pet::getSpecies, Collectors.counting()))
                .entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("species", e.getKey()); m.put("count", e.getValue()); return m; })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPetStatusStats() {
        String[] statusNames = {"待救助", "救助中", "待领养", "已领养", "已归还"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < statusNames.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("status", i);
            m.put("statusName", statusNames[i]);
            m.put("count", petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getStatus, i)));
            result.add(m);
        }
        return result;
    }

    public List<Map<String, Object>> getAdoptionTrend() {
        List<AdoptionApplication> apps = adoptionMapper.selectList(
                new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, 1));
        return apps.stream()
                .collect(Collectors.groupingBy(a -> a.getReviewTime().toLocalDate().getMonth().toString(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("month", e.getKey()); m.put("count", e.getValue()); return m; })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getStationCapacity() {
        return stationMapper.selectList(new LambdaQueryWrapper<RescueStation>().eq(RescueStation::getStatus, 1))
                .stream().map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", s.getId()); m.put("name", s.getName());
                    m.put("capacity", s.getCapacity()); m.put("currentCount", s.getCurrentCount());
                    m.put("usage", s.getCapacity() > 0 ? Math.round(s.getCurrentCount() * 100.0 / s.getCapacity()) : 0);
                    return m;
                }).collect(Collectors.toList());
    }
}
