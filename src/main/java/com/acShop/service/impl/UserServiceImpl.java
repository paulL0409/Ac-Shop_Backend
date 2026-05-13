package com.acShop.service.impl;

import com.acShop.mapper.ShopMapper;
import com.acShop.mapper.UserMapper;
import com.acShop.pojo.PageBean;
import com.acShop.pojo.User;
import com.acShop.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShopMapper shopMapper;

    @Override
    public PageBean page(Integer page, Integer pageSize, String username,  String role) {
        PageHelper.startPage(page,pageSize);
        List<User> userList = userMapper.list(username, role);
        Page<User> p = (Page<User>)userList;
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public void delete(List<Integer> ids) {
        shopMapper.deleteByOwnerIds(ids);
        userMapper.delete(ids);
    }

    @Override
    public void add(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.getUserPassword());
        user.setCreateTime(LocalDateTime.now());
        user.setUserPassword(hashedPassword);
        userMapper.add(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public User login(User user) {
        User dbuser = userMapper.findUserByName(user);
        if(dbuser==null){
            throw new RuntimeException("User not found");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(user.getUserPassword(), dbuser.getUserPassword())){
            throw new RuntimeException("Password incorrect");
        }
        return dbuser;
    }
}
