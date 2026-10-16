package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.LoanApply;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface LoanApplyDao extends BaseDao<LoanApply> {
    List<LoanApply> selectVoList(IPage<LoanApply> page, @Param("userId") Long userId,
                                 @Param("applyNo") String applyNo, @Param("userName") String userName,
                                 @Param("applyStatus") Integer applyStatus);
}
