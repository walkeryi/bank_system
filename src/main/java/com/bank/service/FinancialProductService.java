package com.bank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.entity.FinancialProduct;
import java.util.List;

/** 金融产品 业务接口 */
public interface FinancialProductService extends IService<FinancialProduct> {
    Page<FinancialProduct> pageVo(Page<FinancialProduct> page, String productName, Integer productType, Integer status);
}
