package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.Account;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 账户 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface AccountDao extends BaseDao<Account> {
    List<Account> selectVoList();
    List<Account> selectVoList(IPage<Account> page, @Param("userId") Long userId,
                               @Param("accountNo") String accountNo, @Param("userName") String userName,
                               @Param("idCard") String idCard, @Param("accountStatus") Integer accountStatus);
}
