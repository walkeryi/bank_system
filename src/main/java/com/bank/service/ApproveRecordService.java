package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.ApproveRecord;
import java.util.List;

public interface ApproveRecordService extends IService<ApproveRecord> {
    Page<ApproveRecord> pageVo(Page<ApproveRecord> page, String applyUserName, String approveUserName, Integer approveStatus);
}
