package com.acShop.service.impl;

import com.acShop.mapper.ProductMapper;
import com.acShop.mapper.ShopMapper;
import com.acShop.pojo.PageBean;
import com.acShop.pojo.Result;
import com.acShop.pojo.Shop;
import com.acShop.pojo.User;
import com.acShop.service.ShopService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageBean page(Integer page, Integer pageSize, String name) {
        PageHelper.startPage(page, pageSize);
        List<Shop> shopList = shopMapper.list(name);
        Page<Shop> p = (Page<Shop>) shopList;
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public void delete(List<Integer> ids) {
        productMapper.deleteByShopIds(ids);
        shopMapper.delete(ids);
    }

    @Override
    public void add(Shop shop) {
        shop.setCreateTime(LocalDateTime.now());
        shopMapper.add(shop);
    }

    @Override
    public void update(Shop shop) {
        shopMapper.update(shop);
    }

    @Override
    public Shop getByOwnerId(Long userId) {
        Shop shop = shopMapper.getByOwnerId(userId);
        return shop;
    }

}
