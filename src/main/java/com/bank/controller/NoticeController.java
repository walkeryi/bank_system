package com.bank.controller;

import com.bank.dao.UserRoleDao;
import com.bank.service.AuditLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants;
import com.bank.common.Result;
import com.bank.entity.Notice;
import com.bank.entity.User;
import com.bank.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private AuditLogService auditLogService;

    @Autowired private NoticeService noticeService;

    @GetMapping("/list") public Result<Page<Notice>> list(
            @RequestParam(required = false) String noticeTitle,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize,
            HttpServletRequest req){
        return Result.ok(noticeService.pageVo(new Page<>(pageNum, pageSize), noticeTitle, isRead, curUser(req)));
    }
    private User curUser(HttpServletRequest req){
        return (User) req.getSession().getAttribute(Constants.SESSION_USER);
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody Notice e){ noticeService.save(e); return Result.ok(); }
    @PostMapping("/delete/{id}") public Result<Void> del(@PathVariable Long id){ noticeService.removeById(id); return Result.ok(); }

}
