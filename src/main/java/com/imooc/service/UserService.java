package com.imooc.service;

//import com.imooc.dao.mapper.UserMapper;

import com.imooc.dao.mapper.UserMapper;
import com.imooc.dao.repository.UserRepository;
import com.imooc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    public int addOne(User user)
    {
        return userMapper.insert(user);
    }
    
    public User findById(Long userId)
    {
        return userMapper.selectByPrimaryKey(userId);
    }
    
    public User addOneByRepository(User user)
    {
        return userRepository.save(user);
    }
    
    public User findByIdByRepository(Long userId)
    {
        return userRepository.findOne(userId);
    }
    
}
