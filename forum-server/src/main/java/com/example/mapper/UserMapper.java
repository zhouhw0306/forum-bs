package com.example.mapper;

import com.example.domain.dao.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
* @author zhw
* @description 针对表【tb_user】的数据库操作Mapper
* @createDate 2022-09-27 15:26:49
* @Entity com.example.domain.dao.User
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 添加积分
     */
    @Update("update tb_user set score = score + #{score} where id = #{id}")
    int addScore(String id,int score);

}




