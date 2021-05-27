package com.light.storage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.light.storage.entity.Admin;
import com.light.storage.entity.BirthInfo;
import com.light.storage.mapper.AdminMapper;
import com.light.storage.mapper.BirthInfoMapper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BirthInfoService {

    @Autowired
    BirthInfoMapper birthInfoMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AdminMapper adminMapper;

    public List<BirthInfo> selectAll(int userId) {
        return birthInfoMapper.selectAll(userId);
    }

    public List<BirthInfo> selectByName(String name,int userId) {
        return birthInfoMapper.selectByName(name,userId);
    }

    public int insert(BirthInfo birthInfo) {
       return birthInfoMapper.insert(birthInfo);
    }

    public int updateSelective(BirthInfo birthInfo) {

        return birthInfoMapper.updateSelective(birthInfo);
    }

    public int delete(int id) {
        return birthInfoMapper.delete(id);
    }

    public int getNumOfBir(int userId) {
        return birthInfoMapper.getNumOfBir(userId);
    }

    @Scheduled(cron = "0 0 7 * * ?")
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void findBirthDay() {
        List<BirthInfo> birthDay = birthInfoMapper.getBirthDay();
        String date=new SimpleDateFormat("MM-dd").format(new Date());
        for(BirthInfo bir:birthDay){
            if(bir.getBirth().equals(date)){
                Map<String,String> map=new HashMap<>();
                Admin admin=adminMapper.selectById(bir.getUserId());
                map.put("email",admin.getEmail());
                map.put("name",bir.getName());
                map.put("username",admin.getUsername());
                try {
                    String s = new ObjectMapper().writeValueAsString(map);
                    String msgId = UUID.randomUUID().toString();
                    rabbitTemplate.convertAndSend("mail","birth",s,new CorrelationData(msgId));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
