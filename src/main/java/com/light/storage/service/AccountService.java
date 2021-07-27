package com.light.storage.service;

import com.light.storage.entity.Account;
import com.light.storage.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Cacheable(value = "accounts" ,key="'userId:'+#userId")
    public List<Account> selectAll(int userId) {
        List<Account> accounts = accountMapper.selectAll(userId);
        for (Account account : accounts) {
            String decode =new String(Base64Utils.decodeFromString(account.getPassword())) ;
            account.setPassword(decode);
        }
        return accounts;
    }

    public List<Account> selectByName(String name,int userId) {
        List<Account> accounts = accountMapper.selectByName(name, userId);
        for (Account account : accounts) {
            String decode =new String(Base64Utils.decodeFromString(account.getPassword())) ;
            account.setPassword(decode);
        }
        return accounts;
    }

    @CachePut(value = "accounts" ,key="'userId:'+#account.userId")
    public List<Account> insert(Account account) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        account.setAlterTime(dateFormat.format(new Date()));
        String encode = Base64Utils.encodeToString(account.getPassword().getBytes(StandardCharsets.UTF_8));
        account.setPassword(encode);
        accountMapper.insert(account);
        return selectAll(account.getUserId());
    }
    @CachePut(value = "accounts" ,key="'userId:'+#account.userId")
    public List<Account>  updateSelective(Account account) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        account.setAlterTime(dateFormat.format(new Date()));
        String encode = Base64Utils.encodeToString(account.getPassword().getBytes(StandardCharsets.UTF_8));
        account.setPassword(encode);
         accountMapper.updateSelective(account);
        return selectAll(account.getUserId());
    }

    public int getNumOfAcc(int userId) {
        return accountMapper.getNumOfAcc(userId);
    }

}
