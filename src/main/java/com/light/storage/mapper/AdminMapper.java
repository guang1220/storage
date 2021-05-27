package com.light.storage.mapper;

import com.light.storage.entity.Account;
import com.light.storage.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {
    public Admin selectByName(String username);

    public Admin selectById(@Param("id") int id);

    public Admin selectByEmail(@Param("email") String email);

    public int insert(Admin admin);


    public int updateSelective(Admin admin);
}
