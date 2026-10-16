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
@TableName("large_withdraw_apply")
public class LargeWithdrawApply implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "apply_id", type = IdType.AUTO)
    private Long applyId;
    private String applyNo;
    private Long accountId;
    private BigDecimal withdrawAmount;
    private Long applyTellerId;
    private Integer applyStatus;
    private Long approveManagerId;
    private Date approveTime;
    private String approveComment;
    private Date createTime;
    @TableField(exist = false)
    private String accountNo;
    @TableField(exist = false)
    private String tellerName;
    @TableField(exist = false)
    private String managerName;
    @TableField(exist = false)
    private String balance;
}