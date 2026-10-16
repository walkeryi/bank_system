package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.Menu;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 菜单持久层：按用户角色查询可见菜单 */
public interface MenuDao extends BaseDao<Menu> {
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);
}
