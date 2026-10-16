package com.bank.service;

import com.bank.entity.Menu; import java.util.List;
public interface MenuService { List<Menu> listByUserId(Long userId); }
