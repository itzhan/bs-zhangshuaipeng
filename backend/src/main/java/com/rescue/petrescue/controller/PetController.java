package com.rescue.petrescue.controller;

import com.rescue.petrescue.common.Result;
import com.rescue.petrescue.entity.Pet;
import com.rescue.petrescue.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(required = false) String species,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) Integer gender,
                          @RequestParam(required = false) String keyword) {
        return Result.success(petService.list(page, size, species, status, gender, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(petService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> create(@RequestBody Pet pet) {
        return Result.success(petService.create(pet));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> update(@PathVariable Long id, @RequestBody Pet pet) {
        return Result.success(petService.update(id, pet));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> delete(@PathVariable Long id) {
        petService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        petService.updateStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }
}
