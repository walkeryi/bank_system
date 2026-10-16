package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.AccountStatusChange;
import java.util.List;

public interface AccountStatusChangeService extends IService<AccountStatusChange> {
    Page<AccountStatusChange> pageVo(Page<AccountStatusChange> page, String accountNo, String operateUserName);
}
