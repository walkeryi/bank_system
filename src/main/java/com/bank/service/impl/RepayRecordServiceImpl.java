package com.bank.service.impl;

import com.bank.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.RepayRecordDao;
import com.bank.entity.RepayRecord;
import com.bank.service.RepayRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RepayRecordServiceImpl extends ServiceImpl<RepayRecordDao,RepayRecord> implements RepayRecordService {
    private static final int ROLE_CUSTOMER = 5;
        @Override public Page<RepayRecord> pageVo(Page<RepayRecord> page, String applyNo, String repayNo, Integer repayStatus, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, applyNo, repayNo, repayStatus));
        return page;
    }
    private boolean isStaff(User u){
        if (u == null || u.getRoleIds() == null) return false;
        for (Integer rid : u.getRoleIds()) {
            if (rid != null && rid != ROLE_CUSTOMER) return true;
        }
        return false;
    }
}
