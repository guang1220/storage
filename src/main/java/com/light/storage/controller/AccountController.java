package com.light.storage.controller;

import com.light.storage.entity.Account;
import com.light.storage.service.AccountService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts")
    public List<Account> accounts(@RequestParam("userId") int userId){
        return accountService.selectAll(userId);
    }

    @GetMapping("/accountOfName")
    public List<Account> selectByName(@RequestParam("name") String name,@RequestParam("userId") int userId){
        return accountService.selectByName(name,userId);
    }

    @PostMapping("/addAccount")
    public int insert(Account account) {
        List<Account> insert = accountService.insert(account);
        return insert.size()>0?1:0;
    }

    @PutMapping("/editAccount")
    public int update(Account account) {
        List<Account> accounts = accountService.updateSelective(account);
        return  accounts.size()>0?1:0;
    }

    @GetMapping("/getNumOfAcc")
    public int getNumOfAcc(@RequestParam("userId") int userId){
        return accountService.getNumOfAcc(userId);
    }


}
