package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.AccountStatusChange;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 账户状态变更 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface AccountStatusChangeDao extends BaseDao<AccountStatusChange> {
    List<AccountStatusChange> selectVoList(IPage<AccountStatusChange> page,
                                           @Param("accountNo") String accountNo,
                                           @Param("operateUserName") String operateUserName);
}
