package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.AccountStatusChange;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface AccountStatusChangeDao extends BaseDao<AccountStatusChange> {
    List<AccountStatusChange> selectVoList(IPage<AccountStatusChange> page,
                                           @Param("accountNo") String accountNo,
                                           @Param("operateUserName") String operateUserName);
}
