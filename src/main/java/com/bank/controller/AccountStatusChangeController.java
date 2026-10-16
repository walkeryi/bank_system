package com.bank.controller;

import com.bank.common.Result;
import com.bank.entity.AccountStatusChange;
import com.bank.service.AccountStatusChangeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/status")
public class AccountStatusChangeController {
    @Autowired private AccountStatusChangeService statusService;

    @GetMapping("/list") public Result<Page<AccountStatusChange>> list(
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) String operateUserName,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(statusService.pageVo(new Page<>(pageNum, pageSize), accountNo, operateUserName));
    }
}
