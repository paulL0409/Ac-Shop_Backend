package com.acShop.mapper;

import com.acShop.pojo.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> list(Integer shopId, String name, BigDecimal begin, BigDecimal end);

    void delete(List<Integer> ids);

    void deleteByShopIds(List<Integer> shopIds);

    @Insert("insert into product(shop_id, name, description, price, image_url, create_time)"+
            " values(#{shopId}, #{name}, #{description}, #{price}, #{imageUrl}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(Product product);

    void update(Product product);

    @Select("select * from product where id = #{id}")
    Product getById(Long id);
}
