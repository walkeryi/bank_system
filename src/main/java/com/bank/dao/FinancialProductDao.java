package com.bank.dao;

import com.bank.common.BaseDao;
import com.bank.entity.FinancialProduct;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 金融产品 持久层（单表 CRUD 由 BaseMapper 提供） */
public interface FinancialProductDao extends BaseDao<FinancialProduct> {
    List<FinancialProduct> selectVoList(IPage<FinancialProduct> page,
                                        @Param("productName") String productName,
                                        @Param("productType") Integer productType,
                                        @Param("status") Integer status);
}
