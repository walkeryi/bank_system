package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.RepayRecord;
import com.bank.entity.User;
import java.util.List;

public interface RepayRecordService extends IService<RepayRecord> {
    Page<RepayRecord> pageVo(Page<RepayRecord> page, String applyNo, String repayNo, Integer repayStatus, User curUser);
}
