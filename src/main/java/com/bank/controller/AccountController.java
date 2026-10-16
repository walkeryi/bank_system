package com.bank.controller;

import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.Account; import com.bank.entity.User;
import com.bank.service.AccountService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired private AccountService accountService;

    @GetMapping("/list") public Result<Page<Account>> list(
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) Integer accountStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(accountService.pageVo(new Page<>(pageNum, pageSize), accountNo, userName, idCard, accountStatus, curUser(req)));
    }

    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody Account a, HttpServletRequest req){
        accountService.openAccount(a, curId(req), req.getRemoteAddr()); return Result.ok();
    }
    /** 账户维护：冻结1/解冻0/销户2 */
    @PostMapping("/status") public Result<Void> status(@RequestBody Map<String,Object> b, HttpServletRequest req){
        accountService.changeStatus(Long.valueOf(b.get("id").toString()),
                Integer.valueOf(b.get("afterStatus").toString()),
                b.get("reason")==null?"":b.get("reason").toString(), curId(req), req.getRemoteAddr());
        return Result.ok();
    }
    private Long curId(HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        return u==null?null:u.getUserId();
    }

}
