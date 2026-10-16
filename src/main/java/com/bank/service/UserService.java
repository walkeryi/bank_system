package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.Menu; import com.bank.entity.Role; import com.bank.entity.User; import java.util.List;
public interface UserService extends IService<User> {
    Page<User> pageVo(Page<User> page, String userNo, String userName, String phone, Integer status);
    User login(String userNo, String rawPassword);
    boolean register(String userNo, String userName, String idCard,String phone,String password,int roleId);
    List<Menu> menus(Long userId);
    List<Integer> roleIds(Long userId);
    List<Role> allRoles();
    void addUser(User user, List<Integer> roleIds);
    boolean changePassword(Long userId, String oldPwd, String newPwd);
}
