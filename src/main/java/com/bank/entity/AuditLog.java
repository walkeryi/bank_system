package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("audit_log")
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;
    private Long operatorUserId;
    private String operateModule;
    private String operateType;
    private String businessKey;
    private String operateContent;
    private Date operateTime;
    //private String ipAddr;
    @TableField(exist = false)
    private String operatorUserName;
}