package com.bank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.LoanApply; import com.bank.entity.User;
import com.bank.service.LoanApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List; import java.util.Map;

@RestController
@RequestMapping("/api/loan")
public class LoanApplyController {
    @Autowired private LoanApplyService loanService;

    @GetMapping("/list") public Result<Page<LoanApply>> list(
            @RequestParam(required = false) String applyNo,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer applyStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(loanService.pageVo(new Page<>(pageNum, pageSize), applyNo, userName, applyStatus, curUser(req)));
    }
    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody LoanApply l, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        loanService.apply(l, u.getUserId()); return Result.ok();
    }
    @PostMapping("/audit") public Result<Void> audit(@RequestBody Map<String,Object> b, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        loanService.audit(Long.valueOf(b.get("id").toString()), Boolean.parseBoolean(b.get("pass").toString()), u.getUserId());
        return Result.ok();
    }
    @PostMapping("/approve") public Result<Void> approve(@RequestBody Map<String,Object> b, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        loanService.approve(Long.valueOf(b.get("id").toString()), Boolean.parseBoolean(b.get("pass").toString()),
                b.get("comment")==null?"":b.get("comment").toString(), u.getUserId());
        return Result.ok();
    }
    @PostMapping("/lend/{id}") public Result<Void> lend(@PathVariable Long id, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        loanService.lend(id, u.getUserId(), req.getRemoteAddr()); return Result.ok();
    }
}
