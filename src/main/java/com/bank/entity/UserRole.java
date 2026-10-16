package com.bank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable; import java.util.Date;

@Data
@TableName("user_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value="ur_id", type=IdType.AUTO) private Integer urId;
    private Long userId; private Integer roleId; private Date createTime;
}
