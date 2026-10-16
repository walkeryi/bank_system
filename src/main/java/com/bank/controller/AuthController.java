package com.bank.controller;

import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.Menu; import com.bank.entity.User;
import com.bank.service.MenuService; import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List; import java.util.Map;

/** 登录认证：校验口令 -> 按角色加载菜单 -> 写入 Session */
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private MenuService menuService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String,String> body, HttpSession session){
        User user = userService.login(body.get("userNo"), body.get("password"));
        List<Menu> menus = menuService.listByUserId(user.getUserId());
        session.setAttribute(Constants.SESSION_USER, user);
        session.setAttribute(Constants.SESSION_MENUS, menus);
        return Result.ok(user);
    }
    @GetMapping("/info")
    public Result<User> info(HttpSession session){
        return Result.ok((User) session.getAttribute(Constants.SESSION_USER));
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String,String> body){
        String userNo = body.get("userNo");
        String userName = body.get("userName");
        String idCard = body.get("idCard");
        String phone = body.get("phone");
        String password = body.get("password");
        String roleCode=body.get("roleId");
        if(userNo == null){
            return Result.fail("工号不能为空");
        }
        if(userName == null){
            return Result.fail("用户名不能为空");
        }
        if(password == null || password.length() <6){
            return Result.fail("密码至少6位");
        }
        if(idCard.length()!=18){
            return Result.fail("身份证号码要18位");
        }
        if(phone.length()!=11){
            return Result.fail("手机号码要11位");
        }
        if(roleCode == null){
            return Result.fail("请选择角色");
        }
        Map<String,Integer> roleMap = new HashMap<>();
        roleMap.put("TELLER",1);
        roleMap.put("LOAN",2);
        roleMap.put("MANAGER",3);
        roleMap.put("RISK",4);
        roleMap.put("CUSTOMER",5);
        if(roleCode == null){
            return Result.fail("角色不存在");
        }
        int roleId = roleMap.get(roleCode);
        boolean save = userService.register(userNo, userName, idCard,phone,password,roleId);
        if(save){
            return Result.ok("注册成功");
        }else{
            return Result.fail("账号已存在，注册失败");
        }
    }

}
