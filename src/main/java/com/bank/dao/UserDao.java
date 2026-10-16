package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.apache.ibatis.annotations.Param;


public interface UserDao extends BaseDao<User> {
    List<User> selectVoList(IPage<User> page, @Param("userNo") String userNo, @Param("userName") String userName,
                            @Param("phone") String phone, @Param("status") Integer status);

    User selectByUserNo(@Param("userNo") String userNo);
}
