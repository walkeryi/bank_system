package com.bank.service.impl;

import com.bank.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.dao.AccountDao; import com.bank.dao.TradeRecordDao;
import com.bank.entity.Account; import com.bank.entity.TradeRecord;
import com.bank.service.AuditLogService; import com.bank.service.TradeRecordService;
import com.bank.util.NoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeRecordServiceImpl extends ServiceImpl<TradeRecordDao, TradeRecord> implements TradeRecordService {
    private static final int ROLE_CUSTOMER = 5;
    @Autowired private AccountDao accountDao;
    @Autowired private AuditLogService auditLogService;
        @Override public Page<TradeRecord> pageVo(Page<TradeRecord> page, String tradeNo, String accountNo, Integer tradeType, Integer tradeStatus, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, tradeNo, accountNo, tradeType, tradeStatus));
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
    public void doTrade(TradeRecord t, Long op, String ip){
        if (t.getAccountId()==null || t.getTradeAmount()==null || t.getTradeAmount().compareTo(BigDecimal.ZERO)<=0)
            throw new BusinessException("账户与金额必填且须大于0");
        Account src=accountDao.selectById(t.getAccountId());
        if (src==null) throw new BusinessException("操作账户不存在");
        if (src.getAccountStatus()==2) throw new BusinessException("账户已销户，禁止交易");
        t.setTradeNo(NoGenerator.tradeNo()); t.setTradeStatus(1); t.setCounterUserId(op);
        Integer type=t.getTradeType();
        if (type==1){
            src.setBalance(src.getBalance().add(t.getTradeAmount())); accountDao.updateById(src); baseMapper.insert(t);
        } else if (type==2){
            if (src.getAccountStatus()==1) throw new BusinessException("账户已冻结，禁止取款");
            if (src.getBalance().compareTo(t.getTradeAmount())<0) throw new BusinessException("余额不足");
            src.setBalance(src.getBalance().subtract(t.getTradeAmount())); accountDao.updateById(src); baseMapper.insert(t);
        } else if (type==3){
            if (t.getTargetAccountId()==null) throw new BusinessException("转账必须填写转入账户");
            Account dst=accountDao.selectById(t.getTargetAccountId());
            if (dst==null) throw new BusinessException("转入账户不存在");
            if (dst.getAccountStatus()!=0) throw new BusinessException("转入账户状态异常");
            if (src.getAccountStatus()==1) throw new BusinessException("转出账户已冻结");
            if (src.getBalance().compareTo(t.getTradeAmount())<0) throw new BusinessException("余额不足");
            src.setBalance(src.getBalance().subtract(t.getTradeAmount()));
            dst.setBalance(dst.getBalance().add(t.getTradeAmount()));
            accountDao.updateById(src); accountDao.updateById(dst);
            baseMapper.insert(t);
            TradeRecord in=new TradeRecord();
            in.setTradeNo(NoGenerator.tradeNo()); in.setAccountId(dst.getAccountId()); in.setTradeType(4);
            in.setTradeAmount(t.getTradeAmount()); in.setTradeStatus(1); in.setCounterUserId(op);
            in.setTargetAccountId(src.getAccountId()); in.setRemark("转账转入，来自 "+src.getAccountNo());
            baseMapper.insert(in);
        } else throw new BusinessException("非法交易类型");
        auditLogService.log(op,"柜面交易","交易记账",t.getTradeNo(),"类型"+type+" 金额"+t.getTradeAmount(),ip);
    }
}
