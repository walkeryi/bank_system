package com.bank.controller;

import com.bank.common.Result;
import com.bank.entity.AuditLog;
import com.bank.service.AuditLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {
    @Autowired private AuditLogService auditLogService;

    @GetMapping("/list") public Result<Page<AuditLog>> list(
            @RequestParam(required = false) String operatorUserName,
            @RequestParam(required = false) String operateModule,
            @RequestParam(required = false) String operateType,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(auditLogService.pageVo(new Page<>(pageNum, pageSize), operatorUserName, operateModule, operateType));
    }
}
