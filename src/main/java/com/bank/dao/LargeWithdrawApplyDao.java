package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.LargeWithdrawApply;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 大额取款 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface LargeWithdrawApplyDao extends BaseDao<LargeWithdrawApply> {
    List<LargeWithdrawApply> selectVoList();
    List<LargeWithdrawApply> selectVoList(IPage<LargeWithdrawApply> page, @Param("userId") Long userId,
                                          @Param("applyNo") String applyNo, @Param("accountNo") String accountNo,
                                          @Param("applyStatus") Integer applyStatus);
}
