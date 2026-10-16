package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("account_status_change")
public class AccountStatusChange implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "change_id", type = IdType.AUTO)
    private Long changeId;
    private Long accountId;
    private Integer beforeStatus;
    private Integer afterStatus;
    private Long operateUserId;
    private String changeReason;
    private Date changeTime;
    @TableField(exist = false)
    private String accountNo;
    @TableField(exist = false)
    private String operateUserName;
}