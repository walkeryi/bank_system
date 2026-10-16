package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.ApproveRecordDao;
import com.bank.entity.ApproveRecord;
import com.bank.service.ApproveRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApproveRecordServiceImpl extends ServiceImpl<ApproveRecordDao,ApproveRecord> implements ApproveRecordService {
        @Override public Page<ApproveRecord> pageVo(Page<ApproveRecord> page, String applyUserName, String approveUserName, Integer approveStatus){
        page.setRecords(baseMapper.selectVoList(page, applyUserName, approveUserName, approveStatus));
        return page;
    }
}
