package com.bank.controller;

import com.bank.common.Result;
import com.bank.entity.FinancialProduct;
import com.bank.service.FinancialProductService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class FinancialProductController {
    @Autowired private FinancialProductService productService;
    /** 金融产品：分页 + 按名称模糊搜索 + 类型/状态筛选 */
    @GetMapping("/list") public Result<Page<FinancialProduct>> list(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer productType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(productService.pageVo(new Page<>(pageNum, pageSize), productName, productType, status));
    }
    @PostMapping("/add") public Result<Void> add(@RequestBody FinancialProduct e){ productService.save(e); return Result.ok(); }
    @PostMapping("/delete/{id}") public Result<Void> del(@PathVariable Long id){ productService.removeById(id); return Result.ok(); }
}
