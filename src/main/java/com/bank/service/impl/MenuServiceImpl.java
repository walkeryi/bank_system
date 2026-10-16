package com.bank.service.impl;

import com.bank.dao.MenuDao; import com.bank.entity.Menu;
import com.bank.service.MenuService; import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; import java.util.List;
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired private MenuDao menuDao;
    @Override public List<Menu> listByUserId(Long userId){ return menuDao.selectMenusByUserId(userId); }
}
