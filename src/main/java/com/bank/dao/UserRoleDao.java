package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserRoleDao extends BaseDao<UserRole> {
    List<Integer> selectRoleIdsByUserId(@Param("userId") Long userId);
    int deleteByUserId(@Param("userId") Long userId);
}
