package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.ApproveRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface ApproveRecordDao extends BaseDao<ApproveRecord> {
    List<ApproveRecord> selectVoList(IPage<ApproveRecord> page,
                                     @Param("applyUserName") String applyUserName,
                                     @Param("approveUserName") String approveUserName,
                                     @Param("approveStatus") Integer approveStatus);
}
