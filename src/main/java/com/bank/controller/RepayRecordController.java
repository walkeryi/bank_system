package com.bank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants;
import com.bank.common.Result;
import com.bank.entity.RepayRecord;
import com.bank.entity.User;
import com.bank.service.RepayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/repay")
public class RepayRecordController {
    @Autowired private RepayRecordService repayService;

    @GetMapping("/list") public Result<Page<RepayRecord>> list(
            @RequestParam(required = false) String applyNo,
            @RequestParam(required = false) String repayNo,
            @RequestParam(required = false) Integer repayStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(repayService.pageVo(new Page<>(pageNum, pageSize), applyNo, repayNo, repayStatus, curUser(req)));
    }
    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
}
