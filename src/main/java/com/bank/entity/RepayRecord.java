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
/** 还款记录 实体，表 repay_record */
@TableName("repay_record")
public class RepayRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "repay_id", type = IdType.AUTO)
    private Long repayId;
    private Long loanId;
    private String repayNo;
    private BigDecimal repayAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private Integer repayStatus;
    private Date repayTime;
    private String remark;
    @TableField(exist = false)
    private String applyNo;
}