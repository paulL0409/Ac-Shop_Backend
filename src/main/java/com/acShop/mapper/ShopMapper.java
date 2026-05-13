package com.acShop.mapper;

import com.acShop.pojo.Shop;
import com.acShop.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<Shop> list(String name);

    void delete(List<Integer> ids);

    void deleteByOwnerIds(List<Integer> ownerIds);

    @Insert("insert into shop(owner_id, name, create_time, description, image_url)"+
            " values(#{ownerId}, #{name}, #{createTime}, #{description}, #{imageUrl})")
    void add(Shop shop);

    void update(Shop shop);

    @Select("select * from shop where owner_id = #{userId}")
    Shop getByOwnerId(Long userId);
}
