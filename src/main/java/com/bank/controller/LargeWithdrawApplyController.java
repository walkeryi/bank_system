package com.bank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.LargeWithdrawApply; import com.bank.entity.User;
import com.bank.service.LargeWithdrawApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List; import java.util.Map;

@RestController
@RequestMapping("/api/withdraw")
public class LargeWithdrawApplyController {
    @Autowired private LargeWithdrawApplyService withdrawService;

    @GetMapping("/list") public Result<Page<LargeWithdrawApply>> list(
            @RequestParam(required = false) String applyNo,
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) Integer applyStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(withdrawService.pageVo(new Page<>(pageNum, pageSize), applyNo, accountNo, applyStatus, curUser(req)));
    }
    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody LargeWithdrawApply a, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        withdrawService.apply(a, u.getUserId()); return Result.ok();
    }
    @PostMapping("/approve") public Result<Void> approve(@RequestBody Map<String,Object> b, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        withdrawService.approve(Long.valueOf(b.get("id").toString()),
                Boolean.parseBoolean(b.get("pass").toString()),
                b.get("comment")==null?"":b.get("comment").toString(), u.getUserId(), req.getRemoteAddr());
        return Result.ok();
    }
    @PostMapping("/cancel/{id}") public Result<Void> cancel(@PathVariable Long id, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        withdrawService.cancel(id, u.getUserId()); return Result.ok();
    }
}
