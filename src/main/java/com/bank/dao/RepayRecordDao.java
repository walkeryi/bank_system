package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.RepayRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 还款记录 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface RepayRecordDao extends BaseDao<RepayRecord> {
    List<RepayRecord> selectVoList();
    List<RepayRecord> selectVoList(IPage<RepayRecord> page, @Param("userId") Long userId,
                                   @Param("applyNo") String applyNo, @Param("repayNo") String repayNo,
                                   @Param("repayStatus") Integer repayStatus);
}
