package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.LoanApply;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 贷款业务 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface LoanApplyDao extends BaseDao<LoanApply> {
    List<LoanApply> selectVoList(IPage<LoanApply> page, @Param("userId") Long userId,
                                 @Param("applyNo") String applyNo, @Param("userName") String userName,
                                 @Param("applyStatus") Integer applyStatus);
}
