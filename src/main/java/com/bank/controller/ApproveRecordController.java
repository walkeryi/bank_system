package com.bank.controller;

import com.bank.common.Result;
import com.bank.entity.ApproveRecord;
import com.bank.service.ApproveRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/approve")
public class ApproveRecordController {
    @Autowired private ApproveRecordService approveService;

    @GetMapping("/list") public Result<Page<ApproveRecord>> list(
            @RequestParam(required = false) String applyUserName,
            @RequestParam(required = false) String approveUserName,
            @RequestParam(required = false) Integer approveStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(approveService.pageVo(new Page<>(pageNum, pageSize), applyUserName, approveUserName, approveStatus));
    }
}
