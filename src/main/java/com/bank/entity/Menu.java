package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
/** 菜单；登录后按角色关联 role_menu 查询，渲染到页面左侧 */
@TableName("menu")
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value="menu_id", type=IdType.AUTO) private Integer menuId;
    private String menuName, menuUrl, icon; private Integer parentId, sortNo;
}
