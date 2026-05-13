package com.acShop.mapper;

import com.acShop.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> list(String username,  String role);

    void delete(List<Integer> ids);

    @Insert("insert into user(username, user_password, role, create_time)"+
            " values(#{username}, #{userPassword}, #{role}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(User user);

    void update(User user);

    @Select("select * from user where username = #{username}")
    User findUserByName(User user);
}
