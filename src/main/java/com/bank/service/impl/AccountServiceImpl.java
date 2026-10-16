package com.bank.service.impl;

import com.bank.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.dao.AccountDao; import com.bank.dao.AccountStatusChangeDao;
import com.bank.entity.Account; import com.bank.entity.AccountStatusChange;
import com.bank.service.AccountService; import com.bank.service.AuditLogService;
import com.bank.util.NoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {
    @Autowired private AccountStatusChangeDao changeDao;
    @Autowired private AuditLogService auditLogService;
    private static final int ROLE_CUSTOMER = 5;

        @Override public Page<Account> pageVo(Page<Account> page, String accountNo, String userName, String idCard, Integer accountStatus, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, accountNo, userName, idCard, accountStatus));
        return page;
    }
    private boolean isStaff(User u){
        if (u == null || u.getRoleIds() == null) return false;
        for (Integer rid : u.getRoleIds()) {
            if (rid != null && rid != ROLE_CUSTOMER) return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void openAccount(Account a, Long tellerId, String ip){
        if (a.getUserId()==null) throw new BusinessException("请选择客户");
        if (a.getAccountNo()==null || a.getAccountNo().trim().isEmpty()) a.setAccountNo(NoGenerator.accountNo());
        if (a.getBalance()==null) a.setBalance(java.math.BigDecimal.ZERO);
        a.setAccountStatus(0); a.setOpenTellerId(tellerId);
        baseMapper.insert(a);
        auditLogService.log(tellerId,"柜面交易","开户",a.getAccountNo(),
                "为客户["+a.getUserId()+"]开立账户，初始金额"+a.getBalance(),ip);
    }
    @Override
    @Transactional
    public void changeStatus(Long id, Integer after, String reason, Long op, String ip){
        Account a=baseMapper.selectById(id);
        if (a==null) throw new BusinessException("账户不存在");
        Integer before=a.getAccountStatus();
        a.setAccountStatus(after);
        if (after==2) a.setCloseTime(new Date());
        baseMapper.updateById(a);
        AccountStatusChange c=new AccountStatusChange();
        c.setAccountId(id); c.setBeforeStatus(before); c.setAfterStatus(after); c.setOperateUserId(op); c.setChangeReason(reason);
        changeDao.insert(c);
        auditLogService.log(op,"柜面交易","账户状态变更",a.getAccountNo(),"状态 "+before+"->"+after+"，原因："+reason,ip);
    }
}
