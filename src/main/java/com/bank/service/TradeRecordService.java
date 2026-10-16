package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.TradeRecord;
import java.util.List;
import com.bank.entity.User;
public interface TradeRecordService extends IService<TradeRecord> {
    Page<TradeRecord> pageVo(Page<TradeRecord> page, String tradeNo, String accountNo, Integer tradeType, Integer tradeStatus, User curUser);
    void doTrade(TradeRecord trade, Long operatorId, String ip);
}
