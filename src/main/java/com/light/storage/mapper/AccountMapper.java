package com.light.storage.mapper;

import com.light.storage.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMapper {

    public List<Account> selectAll(int userId);

    public List<Account> selectByName(@Param("name") String name,@Param("userId") int userId);

    public int insert(Account account);


    public int updateSelective(Account account);

    public int getNumOfAcc(int userId);

}
