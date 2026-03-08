package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public Result<?> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @GetMapping("/pet-species")
    public Result<?> petSpecies() {
        return Result.success(dashboardService.getPetSpeciesStats());
    }

    @GetMapping("/pet-status")
    public Result<?> petStatus() {
        return Result.success(dashboardService.getPetStatusStats());
    }

    @GetMapping("/adoption-trend")
    public Result<?> adoptionTrend() {
        return Result.success(dashboardService.getAdoptionTrend());
    }

    @GetMapping("/station-capacity")
    public Result<?> stationCapacity() {
        return Result.success(dashboardService.getStationCapacity());
    }
}
