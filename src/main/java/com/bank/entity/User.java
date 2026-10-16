package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    private String userNo;
    private String userName;
    private String idCard;
    private String phone;
    private String passwordHash;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    @TableField(exist = false)
    private String roleName;
    @TableField(exist = false)
    private List<Integer> roleIds;
    @TableField(exist = false)
    private List<Role> roles;
}