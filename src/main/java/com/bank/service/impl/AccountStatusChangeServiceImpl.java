package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.AccountStatusChangeDao;
import com.bank.entity.AccountStatusChange;
import com.bank.service.AccountStatusChangeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountStatusChangeServiceImpl extends ServiceImpl<AccountStatusChangeDao,AccountStatusChange> implements AccountStatusChangeService {
        @Override public Page<AccountStatusChange> pageVo(Page<AccountStatusChange> page, String accountNo, String operateUserName){
        page.setRecords(baseMapper.selectVoList(page, accountNo, operateUserName));
        return page;
    }
}
