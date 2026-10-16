package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("account")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "account_id", type = IdType.AUTO)
    private Long accountId;
    private String accountNo;
    private Long userId;
    private BigDecimal balance;
    private Integer accountStatus;
    private Long openTellerId;
    private Date openTime;
    private Date closeTime;
    private Date updateTime;
    private String remark;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String idCard;
}