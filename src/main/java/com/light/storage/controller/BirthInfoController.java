package com.light.storage.controller;

import com.light.storage.entity.BirthInfo;
import com.light.storage.service.BirthInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class BirthInfoController {
    @Autowired
    BirthInfoService birthInfoService;

    @GetMapping("/birthInfo")
    public List<BirthInfo> birthInfo(@RequestParam("userId") int userId) {
        return birthInfoService.selectAll(userId);
    }

    @GetMapping("/birthOfName")
    public List<BirthInfo> selectByName(@RequestParam("name") String name, @RequestParam("userId") int userId) {
        return birthInfoService.selectByName(name, userId);
    }

    @PostMapping("/addBirth")
    public int insert(BirthInfo birthInfo) {
        return birthInfoService.insert(birthInfo);
    }

    @PutMapping("/editBirth")
    public int update(BirthInfo birthInfo) {
        return birthInfoService.updateSelective(birthInfo);
    }

    @DeleteMapping("/deleteBirth")
    public int delete(@RequestParam("id") int id) {
        return birthInfoService.delete(id);
    }

    @GetMapping("/getNumOfBir")
    public int getNumOfBir(@RequestParam("userId") int userId) {
        return birthInfoService.getNumOfBir(userId);
    }



}
