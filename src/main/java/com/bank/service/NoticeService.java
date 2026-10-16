package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.Notice;
import com.bank.entity.User;
import java.util.List;

/** 通知公告 业务接口 */
public interface NoticeService extends IService<Notice> {
    Page<Notice> pageVo(Page<Notice> page, String noticeTitle, Integer isRead, User curUser);
}
