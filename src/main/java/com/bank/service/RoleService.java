package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.Role;
import java.util.List;

public interface RoleService extends IService<Role> {
    Page<Role> pageVo(Page<Role> page, String roleName);
}
