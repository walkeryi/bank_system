package com.bank.service.impl;

import com.bank.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.NoticeDao;
import com.bank.entity.Notice;
import com.bank.service.NoticeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeDao,Notice> implements NoticeService {
    private static final int ROLE_CUSTOMER = 5;
        @Override public Page<Notice> pageVo(Page<Notice> page, String noticeTitle, Integer isRead, User curUser){
        Long filterUserId = isStaff(curUser) ? null : curUser.getUserId();
        page.setRecords(baseMapper.selectVoList(page, filterUserId, noticeTitle, isRead));
        return page;
    }
    private boolean isStaff(User u){
        if (u == null || u.getRoleIds() == null) return false;
        for (Integer rid : u.getRoleIds()) {
            if (rid != null && rid != ROLE_CUSTOMER) return true;
        }
        return false;
    }
}
