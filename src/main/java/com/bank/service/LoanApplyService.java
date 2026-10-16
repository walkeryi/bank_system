package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.LoanApply;
import com.bank.entity.User;
import java.util.List;
public interface LoanApplyService extends IService<LoanApply> {
    Page<LoanApply> pageVo(Page<LoanApply> page, String applyNo, String userName, Integer applyStatus, User curUser);
    void apply(LoanApply loan, Long creditUserId);
    void audit(Long id, boolean pass, Long creditUserId);
    void approve(Long id, boolean pass, String comment, Long managerId);
    void lend(Long id, Long managerId, String ip);
}
