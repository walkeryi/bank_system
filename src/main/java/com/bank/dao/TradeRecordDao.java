package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.TradeRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 交易流水 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface TradeRecordDao extends BaseDao<TradeRecord> {
    List<TradeRecord> selectVoList();
    List<TradeRecord> selectVoList(IPage<TradeRecord> page, @Param("userId") Long userId,
                                   @Param("tradeNo") String tradeNo, @Param("accountNo") String accountNo,
                                   @Param("tradeType") Integer tradeType, @Param("tradeStatus") Integer tradeStatus);
}
