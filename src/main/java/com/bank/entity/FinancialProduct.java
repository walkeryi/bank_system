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
/** 金融产品 实体，表 financial_product */
@TableName("financial_product")
public class FinancialProduct implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;
    private String productCode;
    private String productName;
    private Integer productType;
    private Integer riskLevel;
    private BigDecimal minInvest;
    private BigDecimal expectedRate;
    private Integer productTerm;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
}