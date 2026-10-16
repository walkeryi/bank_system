package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.ApproveRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 审批记录 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface ApproveRecordDao extends BaseDao<ApproveRecord> {
    List<ApproveRecord> selectVoList(IPage<ApproveRecord> page,
                                     @Param("applyUserName") String applyUserName,
                                     @Param("approveUserName") String approveUserName,
                                     @Param("approveStatus") Integer approveStatus);
}
