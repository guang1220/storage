package com.light.storage.mapper;

import com.light.storage.entity.BirthInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthInfoMapper {

    public List<BirthInfo> selectAll(@Param("userId") int userId);

    public List<BirthInfo> selectByName(@Param("name") String name,@Param("userId") int userId);

    public int insert(BirthInfo birthInfo);


    public int updateSelective(BirthInfo birthInfo);

    public int delete(int id);

    public int getNumOfBir(@Param("userId") int userId);

    public List<BirthInfo> getBirthDay();
}
