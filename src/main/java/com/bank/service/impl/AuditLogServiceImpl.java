package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.AuditLogDao; import com.bank.entity.AuditLog;
import com.bank.service.AuditLogService; import org.springframework.stereotype.Service; import java.util.List;
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogDao, AuditLog> implements AuditLogService {
        @Override public Page<AuditLog> pageVo(Page<AuditLog> page, String operatorUserName, String operateModule, String operateType){
        page.setRecords(baseMapper.selectVoList(page, operatorUserName, operateModule, operateType));
        return page;
    }
    @Override public void log(Long op, String module, String type, String key, String content, String ip){
        AuditLog l=new AuditLog();
        l.setOperatorUserId(op); l.setOperateModule(module); l.setOperateType(type);
        l.setBusinessKey(key); l.setOperateContent(content);
        baseMapper.insert(l);
    }
}
