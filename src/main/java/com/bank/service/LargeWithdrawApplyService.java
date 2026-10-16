package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.LargeWithdrawApply;
import com.bank.entity.User;
import java.util.List;
public interface LargeWithdrawApplyService extends IService<LargeWithdrawApply> {
    Page<LargeWithdrawApply> pageVo(Page<LargeWithdrawApply> page, String applyNo, String accountNo, Integer applyStatus, User curUser);
    void apply(LargeWithdrawApply apply, Long tellerId);
    void approve(Long id, boolean pass, String comment, Long managerId, String ip);
    void cancel(Long id, Long tellerId);
}
