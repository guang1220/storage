package com.light.storage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.light.storage.entity.Admin;
import com.light.storage.mapper.AdminMapper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminService implements UserDetailsService {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AdminMapper adminMapper;

    //    一个支持缓存的方法，在对象内部被调用是不会触发缓存功能的。
    @Cacheable(value = "admin", key = "'userId:'+#result.id")
    public Admin selectByName(String username) {
        return adminMapper.selectByName(username);
    }

    @Cacheable(value = "admin", key = "'userId:'+#id")
    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }

    @CachePut(value = "admin", key = "'userId:'+#result.id")
    public Admin updateSelective(Admin admin) {
         adminMapper.updateSelective(admin);
         return adminMapper.selectById(admin.getId());
    }

    public int findPass(String email, String code) {
        Admin admin = adminMapper.selectByEmail(email);
        if (admin == null) return 0;
        String msgId = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("code", code);
        try {
            String s = new ObjectMapper().writeValueAsString(map);
            rabbitTemplate.convertAndSend("mail", "findPass", s, new CorrelationData(msgId));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int findAndAlterPass(String email, String newPass) {
        Admin admin = adminMapper.selectByEmail(email);
        admin.setPassword(new BCryptPasswordEncoder().encode(newPass));
        return adminMapper.updateSelective(admin);
    }

    public int alterPass(String oldPass, String newPass, int userId) {
        Admin admin = adminMapper.selectById(userId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(oldPass, admin.getPassword())) {
            admin.setPassword(new BCryptPasswordEncoder().encode(newPass));
            return adminMapper.updateSelective(admin);
        }
        return 0;
    }


    public int insert(Admin admin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Admin admin1 = adminMapper.selectByName(admin.getUsername());
        if (admin1 != null) return 3;
        admin1=adminMapper.selectByEmail(admin.getEmail());
        if(admin1!=null) return 4;
        admin.setCreateTime(dateFormat.format(new Date()));
        String msgId = UUID.randomUUID().toString();
        try {
            String s = new ObjectMapper().writeValueAsString(admin);
            rabbitTemplate.convertAndSend("mail", "insert", s, new CorrelationData(msgId));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        admin.setPassword(new BCryptPasswordEncoder().encode(admin.getPassword()));
        return adminMapper.insert(admin);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminMapper.selectByName(username);
        if (admin == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
//        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admins");
        return admin;
    }
}
