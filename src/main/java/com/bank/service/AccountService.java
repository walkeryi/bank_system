package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.Account;
import java.util.List;
import com.bank.entity.User;
public interface AccountService extends IService<Account> {
    Page<Account> pageVo(Page<Account> page, String accountNo, String userName, String idCard, Integer accountStatus, User curUser);
    void openAccount(Account account, Long tellerId, String ip);
    void changeStatus(Long accountId, Integer afterStatus, String reason, Long operatorId, String ip);
}
