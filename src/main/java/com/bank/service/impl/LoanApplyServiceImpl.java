package com.bank.service.impl;

import com.bank.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.dao.ApproveRecordDao; import com.bank.dao.LoanApplyDao;
import com.bank.entity.ApproveRecord; import com.bank.entity.LoanApply;
import com.bank.service.AuditLogService; import com.bank.service.LoanApplyService;
import com.bank.util.NoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar; import java.util.Date; import java.util.List;

@Service
public class LoanApplyServiceImpl extends ServiceImpl<LoanApplyDao, LoanApply> implements LoanApplyService {
    @Autowired private ApproveRecordDao approveRecordDao;
    @Autowired private AuditLogService auditLogService;
    private static final int ROLE_CUSTOMER = 5;
        @Override public Page<LoanApply> pageVo(Page<LoanApply> page, String applyNo, String userName, Integer applyStatus, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, applyNo, userName, applyStatus));
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
    public void apply(LoanApply l, Long creditUserId){
        if (l.getUserId()==null || l.getLoanAmount()==null) throw new BusinessException("借款人与金额必填");
        l.setApplyNo(NoGenerator.loanNo()); l.setApplyStatus(0); l.setCreditUserId(creditUserId);
        baseMapper.insert(l);
    }
    @Override
    @Transactional
    public void audit(Long id, boolean pass, Long creditUserId){
        LoanApply l=baseMapper.selectById(id);
        if (l==null || l.getApplyStatus()!=0) throw new BusinessException("贷款状态不允许初审");
        l.setCreditUserId(creditUserId); l.setApplyStatus(pass?1:3); baseMapper.updateById(l);
    }
    @Override
    @Transactional
    public void approve(Long id, boolean pass, String comment, Long managerId){
        LoanApply l=baseMapper.selectById(id);
        if (l==null || l.getApplyStatus()!=1) throw new BusinessException("贷款状态不允许终审");
        l.setManagerUserId(managerId); l.setApproveTime(new Date()); l.setApplyStatus(pass?2:3);
        baseMapper.updateById(l);
        ApproveRecord r=new ApproveRecord();
        r.setBusinessType("贷款申请"); r.setBusinessId(id); r.setApplyUserId(l.getCreditUserId());
        r.setApproveUserId(managerId); r.setApproveStatus(pass?1:2); r.setApproveOpinion(comment); r.setApproveTime(new Date());
        approveRecordDao.insert(r);
    }
    @Override
    @Transactional
    public void lend(Long id, Long managerId, String ip){
        LoanApply l=baseMapper.selectById(id);
        if (l==null || l.getApplyStatus()!=2) throw new BusinessException("仅审批通过的贷款可放款");
        Date now=new Date(); Calendar cal=Calendar.getInstance(); cal.setTime(now); cal.add(Calendar.MONTH,l.getLoanTerm());
        l.setApplyStatus(4); l.setLoanStartTime(now); l.setLoanEndTime(cal.getTime()); baseMapper.updateById(l);
        auditLogService.log(managerId,"信贷","贷款放款",l.getApplyNo(),
                "放款金额"+l.getLoanAmount()+"，期限"+l.getLoanTerm()+"月",ip);
    }
}
