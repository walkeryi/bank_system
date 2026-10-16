package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.Notice;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface NoticeDao extends BaseDao<Notice> {
    List<Notice> selectVoList();
    List<Notice> selectVoList(IPage<Notice> page, @Param("userId") Long userId,
                              @Param("noticeTitle") String noticeTitle, @Param("isRead") Integer isRead);
}
