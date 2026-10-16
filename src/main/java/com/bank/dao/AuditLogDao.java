package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.AuditLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AuditLogDao extends BaseDao<AuditLog> {
    List<AuditLog> selectVoList(IPage<AuditLog> page,
                                @Param("operatorUserName") String operatorUserName,
                                @Param("operateModule") String operateModule,
                                @Param("operateType") String operateType);
}
