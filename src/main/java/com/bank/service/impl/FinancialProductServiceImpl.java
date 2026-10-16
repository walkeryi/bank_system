package com.bank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.dao.FinancialProductDao;
import com.bank.entity.FinancialProduct;
import com.bank.service.FinancialProductService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinancialProductServiceImpl extends ServiceImpl<FinancialProductDao,FinancialProduct> implements FinancialProductService {
        @Override public Page<FinancialProduct> pageVo(Page<FinancialProduct> page, String productName, Integer productType, Integer status){
        page.setRecords(baseMapper.selectVoList(page, productName, productType, status));
        return page;
    }
}
