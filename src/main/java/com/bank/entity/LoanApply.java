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
@TableName("loan_apply")
public class LoanApply implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "loan_id", type = IdType.AUTO)
    private Long loanId;
    private String applyNo;
    private Long userId;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private BigDecimal rate;
    private String loanPurpose;
    private Integer applyStatus;
    private Long creditUserId;
    private Long managerUserId;
    private Date approveTime;
    private Date loanStartTime;
    private Date loanEndTime;
    private String remark;
    private Date createTime;
    private Date updateTime;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String creditUserName;
    @TableField(exist = false)
    private String managerUserName;
}