package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.AuditLog; import java.util.List;
public interface AuditLogService extends IService<AuditLog> {
    Page<AuditLog> pageVo(Page<AuditLog> page, String operatorUserName, String operateModule, String operateType);
    void log(Long operatorId, String module, String type, String key, String content, String ip);
}
