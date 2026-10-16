package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.RepayRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RepayRecordDao extends BaseDao<RepayRecord> {
    List<RepayRecord> selectVoList();
    List<RepayRecord> selectVoList(IPage<RepayRecord> page, @Param("userId") Long userId,
                                   @Param("applyNo") String applyNo, @Param("repayNo") String repayNo,
                                   @Param("repayStatus") Integer repayStatus);
}
