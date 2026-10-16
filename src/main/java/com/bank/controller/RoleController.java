package com.bank.controller;

import com.bank.common.Result;
import com.bank.entity.Role;
import com.bank.service.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired private RoleService roleService;

    @GetMapping("/list") public Result<Page<Role>> list(
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(roleService.pageVo(new Page<>(pageNum, pageSize), roleName));
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody Role e){ roleService.save(e); return Result.ok(); }
    @PostMapping("/delete/{id}") public Result<Void> del(@PathVariable Long id){ roleService.removeById(id); return Result.ok(); }
}
