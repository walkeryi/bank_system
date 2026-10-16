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
@TableName("trade_record")
public class TradeRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "trade_id", type = IdType.AUTO)
    private Long tradeId;
    private String tradeNo;
    private Long accountId;
    private Integer tradeType;
    private BigDecimal tradeAmount;
    private Integer tradeStatus;
    private Long counterUserId;
    private Long targetAccountId;
    private Date tradeTime;
    private String remark;
    @TableField(exist = false)
    private String accountNo;
    @TableField(exist = false)
    private String targetAccountNo;
    @TableField(exist = false)
    private String counterUserName;
}