package com.bank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.TradeRecord; import com.bank.entity.User;
import com.bank.service.TradeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeRecordController {
    @Autowired private TradeRecordService tradeService;

    @GetMapping("/list") public Result<Page<TradeRecord>> list(
            @RequestParam(required = false) String tradeNo,
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) Integer tradeType,
            @RequestParam(required = false) Integer tradeStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(tradeService.pageVo(new Page<>(pageNum, pageSize), tradeNo, accountNo, tradeType, tradeStatus, curUser(req)));
    }
    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody TradeRecord t, HttpServletRequest req){
        User u=(User) req.getSession().getAttribute(Constants.SESSION_USER);
        tradeService.doTrade(t, u.getUserId(), req.getRemoteAddr()); return Result.ok();
    }
}
