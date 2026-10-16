package com.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;


@Controller
public class PageController {
    @GetMapping("/") public String root(){ return "redirect:/index"; }
    @GetMapping("/register") public String register(){ return "register"; }
    @GetMapping("/login") public String login(){ return "login"; }
    @GetMapping("/logout") public String logout(HttpSession s){ s.invalidate(); return "redirect:/login"; }
    @GetMapping({"/index","/success"}) public String index(){ return "index"; }

    @GetMapping("/user/list") public String userList(){ return "user/list"; }
    @GetMapping("/user/add") public String userAdd(){ return "user/add"; }
    @GetMapping("/user/toPassword") public String pwd(){ return "userPassword"; }
    @GetMapping("/role/list") public String roleList(){ return "role/list"; }
    @GetMapping("/role/add") public String roleAdd(){ return "role/add"; }

    @GetMapping("/account/list") public String accountList(){ return "account/list"; }
    @GetMapping("/account/toAdd") public String accountAdd(){ return "account/add"; }

    @GetMapping("/trade/list") public String tradeList(){ return "trade/list"; }
    @GetMapping("/trade/toAdd") public String tradeAdd(){ return "trade/add"; }

    @GetMapping("/withdraw/list") public String wList(){ return "withdraw/list"; }
    @GetMapping("/withdraw/toAdd") public String wAdd(){ return "withdraw/add"; }

    @GetMapping("/approve/list") public String approveList(){ return "approve/list"; }

    @GetMapping("/loan/list") public String loanList(){ return "loan/list"; }
    @GetMapping("/loan/toAdd") public String loanAdd(){ return "loan/add"; }
    @GetMapping("/repay/list") public String repayList(){ return "repay/list"; }

    @GetMapping("/product/list") public String productList(){ return "product/list"; }
    @GetMapping("/product/toAdd") public String productAdd(){ return "product/add"; }
    @GetMapping("/order/list") public String orderList(){ return "order/list"; }
    @GetMapping("/order/toAdd") public String orderAdd(){ return "order/add"; }

    @GetMapping("/audit/list") public String auditList(){ return "audit/list"; }
    @GetMapping("/status/list") public String statusList(){ return "status/list"; }
    @GetMapping("/notice/list") public String noticeList(){ return "notice/list"; }
    @GetMapping("/notice/add") public String noticeAdd(){ return "notice/add"; }
}
