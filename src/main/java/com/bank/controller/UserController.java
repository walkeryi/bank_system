package com.bank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.common.Constants; import com.bank.common.Result;
import com.bank.entity.Role; import com.bank.entity.User;
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List; import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/list") public Result<Page<User>> list(
            @RequestParam(required = false) String userNo,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "5") long pageSize){
        return Result.ok(userService.pageVo(new Page<>(pageNum, pageSize), userNo, userName, phone, status));
    }
    @GetMapping("/roles") public Result<List<Role>> roles(){ return Result.ok(userService.allRoles()); }
    @PostMapping("/add") public Result<Void> add(@RequestBody User u){
        userService.addUser(u, u.getRoleIds()); return Result.ok();
    }
    @PostMapping("/delete/{id}") public Result<Void> del(@PathVariable Long id){
        userService.removeById(id); return Result.ok();
    }
    @PostMapping("/password") public Result<Void> pwd(@RequestBody Map<String,String> b, HttpSession s){
        User cur=(User) s.getAttribute(Constants.SESSION_USER);
        boolean ok=userService.changePassword(cur.getUserId(), b.get("oldPwd"), b.get("newPwd"));
        return ok?Result.ok():Result.fail("原密码不正确");
    }
}
