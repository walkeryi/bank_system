package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("approve_record")
public class ApproveRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "approve_id", type = IdType.AUTO)
    private Long approveId;
    private String businessType;
    private Long businessId;
    private Long applyUserId;
    private Long approveUserId;
    private Integer approveStatus;
    private String approveOpinion;
    private Date createTime;
    private Date approveTime;
    @TableField(exist = false)
    private String applyUserName;
    @TableField(exist = false)
    private String approveUserName;
}