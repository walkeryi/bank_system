package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.RoleDao;
import com.bank.entity.Role;
import com.bank.service.RoleService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao,Role> implements RoleService {
        @Override public Page<Role> pageVo(Page<Role> page, String roleName){
        page.setRecords(baseMapper.selectVoList(page, roleName));
        return page;
    }
}
