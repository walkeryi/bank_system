package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.BusinessException;
import com.bank.dao.*;
import com.bank.entity.*;
import com.bank.service.UserService;
import com.bank.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private RoleDao roleDao;

        @Override public Page<User> pageVo(Page<User> page, String userNo, String userName, String phone, Integer status){
        page.setRecords(baseMapper.selectVoList(page, userNo, userName, phone, status));
        return page;
    }

    @Override
    public User login(String userNo, String rawPassword){
        User u = baseMapper.selectByUserNo(userNo);
        if (u == null) throw new BusinessException("账号不存在");
        if (u.getStatus()!=null && u.getStatus()==0) throw new BusinessException("账号已禁用，请联系管理员");
        if (!Md5Util.md5(rawPassword).equalsIgnoreCase(u.getPasswordHash())) throw new BusinessException("账号或密码错误");
        u.setPasswordHash(null);
        u.setRoleIds(userRoleDao.selectRoleIdsByUserId(u.getUserId()));
        return u;
    }

    @Transactional
    public boolean register(String userNo, String userName, String idCard,String phone,String password, int roleId) {
        User exist = baseMapper.selectByUserNo(userNo);
        if(exist != null){
            throw new BusinessException("账号已存在");
        }
        User user = new User();
        user.setUserNo(userNo);
        user.setUserName(userName);
        user.setIdCard(idCard);
        user.setPhone(phone);
        user.setPasswordHash(Md5Util.md5(password));
        user.setStatus(1);

        int rows = baseMapper.insert(user);
        if(rows <= 0){
            return false;
        }
        // 插入user_role
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        insertUserRole(user, roleIds);
        return true;
    }

    private void insertUserRole(User u, List<Integer> roleIds){
        userRoleDao.deleteByUserId(u.getUserId());
        if (roleIds != null) {
            for (Integer rid : roleIds) {
                UserRole ur=new UserRole();
                ur.setUserId(u.getUserId());
                ur.setRoleId(rid);
                userRoleDao.insert(ur);
            }
        }
    }

    @Override
    public List<Menu> menus(Long userId){
        return menuDao.selectMenusByUserId(userId);
    }

    @Override
    public List<Integer> roleIds(Long userId){
        return userRoleDao.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<Role> allRoles(){
        return roleDao.selectList(null);
    }

    @Override
    @Transactional
    public void addUser(User u, List<Integer> roleIds){
        if (u.getStatus()==null) u.setStatus(1);
        u.setPasswordHash(Md5Util.md5("123456"));
        baseMapper.insert(u);
        userRoleDao.deleteByUserId(u.getUserId());
        if (roleIds!=null) for (Integer rid: roleIds){
            UserRole ur=new UserRole();
            ur.setUserId(u.getUserId());
            ur.setRoleId(rid);
            userRoleDao.insert(ur);
        }
    }

    @Override
    public boolean changePassword(Long userId, String oldPwd, String newPwd){
        User db = baseMapper.selectById(userId);
        if (db==null || !Md5Util.md5(oldPwd).equalsIgnoreCase(db.getPasswordHash())) return false;
        User u=new User();
        u.setUserId(userId);
        u.setPasswordHash(Md5Util.md5(newPwd));
        baseMapper.updateById(u);
        return true;
    }
}
