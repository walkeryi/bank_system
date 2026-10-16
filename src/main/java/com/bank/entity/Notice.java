package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
/** 通知公告 实体，表 notice */
@TableName("notice")
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;
    private Long receiveUserId;
    private String noticeTitle;
    private String noticeContent;
    private Integer isRead;
    private Date createTime;
    @TableField(exist = false)
    private String receiveUserName;
}