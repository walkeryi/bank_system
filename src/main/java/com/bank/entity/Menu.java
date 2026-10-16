package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("menu")
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value="menu_id", type=IdType.AUTO) private Integer menuId;
    private String menuName, menuUrl, icon; private Integer parentId, sortNo;
}
