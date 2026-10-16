package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.dao.*;
import com.bank.entity.*;
import com.bank.service.AuditLogService; import com.bank.service.LargeWithdrawApplyService;
import com.bank.util.NoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.util.Date; import java.util.List;

@Service
public class LargeWithdrawApplyServiceImpl extends ServiceImpl<LargeWithdrawApplyDao, LargeWithdrawApply>
        implements LargeWithdrawApplyService {
    @Autowired private AccountDao accountDao;
    @Autowired private TradeRecordDao tradeRecordDao;
    @Autowired private ApproveRecordDao approveRecordDao;
    @Autowired private AuditLogService auditLogService;
    private static final int ROLE_CUSTOMER = 5;
        @Override public Page<LargeWithdrawApply> pageVo(Page<LargeWithdrawApply> page, String applyNo, String accountNo, Integer applyStatus, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, applyNo, accountNo, applyStatus));
        return page;
    }
    private boolean isStaff(User u){
        if (u == null || u.getRoleIds() == null) return false;
        for (Integer rid : u.getRoleIds()) {
            if (rid != null && rid != ROLE_CUSTOMER) return true;
        }
        return false;
    }
    @Override @Transactional
    public void apply(LargeWithdrawApply w, Long tellerId){
        Account a=accountDao.selectById(w.getAccountId());
        if (a==null) throw new BusinessException("取款账户不存在");
        if (w.getWithdrawAmount()==null || w.getWithdrawAmount().compareTo(BigDecimal.ZERO)<=0)
            throw new BusinessException("取款金额非法");
        w.setApplyNo(NoGenerator.applyNo()); w.setApplyTellerId(tellerId); w.setApplyStatus(0);
        baseMapper.insert(w);
        ApproveRecord r=new ApproveRecord();
        r.setBusinessType("大额取款"); r.setBusinessId(w.getApplyId()); r.setApplyUserId(tellerId); r.setApproveStatus(0);
        approveRecordDao.insert(r);
    }
    @Override @Transactional
    public void approve(Long id, boolean pass, String comment, Long managerId, String ip){
        LargeWithdrawApply w=baseMapper.selectById(id);
        if (w==null) throw new BusinessException("申请不存在");
        if (w.getApplyStatus()!=0) throw new BusinessException("该申请已处理，不能重复授权");
        w.setApproveManagerId(managerId); w.setApproveTime(new Date()); w.setApproveComment(comment);
        w.setApplyStatus(pass?1:2); baseMapper.updateById(w);
        ApproveRecord r=new ApproveRecord();
        r.setBusinessType("大额取款"); r.setBusinessId(id); r.setApplyUserId(w.getApplyTellerId());
        r.setApproveUserId(managerId); r.setApproveStatus(pass?1:2); r.setApproveOpinion(comment); r.setApproveTime(new Date());
        approveRecordDao.insert(r);
        if (pass){
            Account a=accountDao.selectById(w.getAccountId());
            if (a.getBalance().compareTo(w.getWithdrawAmount())<0) throw new BusinessException("账户余额不足，无法完成大额取款");
            a.setBalance(a.getBalance().subtract(w.getWithdrawAmount())); accountDao.updateById(a);
            TradeRecord t=new TradeRecord();
            t.setTradeNo(NoGenerator.tradeNo()); t.setAccountId(a.getAccountId()); t.setTradeType(2);
            t.setTradeAmount(w.getWithdrawAmount()); t.setTradeStatus(1); t.setCounterUserId(managerId);
            t.setRemark("大额取款，申请单 "+w.getApplyNo()); tradeRecordDao.insert(t);
        }
        auditLogService.log(managerId,"授权审批","大额取款授权",w.getApplyNo(),
                (pass?"通过":"驳回")+" 金额"+w.getWithdrawAmount()+" "+comment,ip);
    }
    @Override @Transactional
    public void cancel(Long id, Long tellerId){
        LargeWithdrawApply w=baseMapper.selectById(id);
        if (w==null) throw new BusinessException("申请不存在");
        if (w.getApplyStatus()!=0) throw new BusinessException("已授权或已驳回的申请不能撤销");
        w.setApplyStatus(3); baseMapper.updateById(w);
    }
}
