package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.TradeRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface TradeRecordDao extends BaseDao<TradeRecord> {
    List<TradeRecord> selectVoList();
    List<TradeRecord> selectVoList(IPage<TradeRecord> page, @Param("userId") Long userId,
                                   @Param("tradeNo") String tradeNo, @Param("accountNo") String accountNo,
                                   @Param("tradeType") Integer tradeType, @Param("tradeStatus") Integer tradeStatus);
}
