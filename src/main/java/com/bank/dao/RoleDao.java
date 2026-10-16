package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface RoleDao extends BaseDao<Role> {
    List<Role> selectVoList(IPage<Role> page, @Param("roleName") String roleName);
}
