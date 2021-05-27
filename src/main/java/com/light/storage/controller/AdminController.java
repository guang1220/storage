package com.light.storage.controller;

import com.light.storage.entity.Admin;
import com.light.storage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Random;

@CrossOrigin
@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/getAdmin")
    public Admin selectById(@RequestParam("id") int id) {
        return adminService.selectById(id);
    }
    @PutMapping("/editAdmin")
    public int editAdmin(Admin admin){
        Admin i = adminService.updateSelective(admin);
        return i!=null?1:0;
    }

    @PostMapping("/sign")
    public int insert(Admin admin){
        return adminService.insert(admin);
    }

    @GetMapping("/findPass")
    public int findPass(@RequestParam("email") String email, HttpServletRequest request){
        Random random=new Random();
        char[] c=new char[4];
        for (int i = 0; i < 4; i++) {
            c[i]=(char) (random.nextInt(122 - 97 + 1) + 97);
        }
        String code=new String(c);
        HttpSession session = request.getSession();
        session.setAttribute("code",code);
        return adminService.findPass(email.trim(),code);
    }

    @PostMapping("/setPass")
    public int findAndAlterPass(@RequestParam("email") String email,@RequestParam("newPass") String newPass,@RequestParam("code") String code,HttpServletRequest request){
        HttpSession session = request.getSession();
        Object code1 = session.getAttribute("code");
        if(code.equals(code1))
        return adminService.findAndAlterPass(email,newPass);
        else return 0;
    }

    @PutMapping("/alterPass")
    public int alterPass(@RequestParam("oldPass") String oldPass,@RequestParam("newPass") String newPass,@RequestParam("userId") int userId){
      return adminService.alterPass(oldPass,newPass,userId);
    }

}
