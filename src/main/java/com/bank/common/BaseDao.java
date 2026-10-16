package com.bank.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/** 持久层通用接口 BaseDao：继承 MyBatis-Plus BaseMapper，自动获得 insert/updateById/deleteById/
 * selectById/selectList/selectCount/selectPage 等单表 CRUD；个性化关联查询写在 mapper/*.xml */
public interface BaseDao<T> extends BaseMapper<T> {
}
