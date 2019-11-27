package com.controller.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.user.User;
import com.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class UserRedis {
    //@Resource
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 存入一个对象
     * @param key
     * @param time
     * @param user
     */
    public void add(String key, Long time, User user){
        Gson gson = new Gson();
        redisTemplate.opsForValue().set(key,gson.toJson(user),time, TimeUnit.MINUTES);
    }
    /**
     * 存入一个列表的对象
     * @param key
     * @param time
     * @param users
     */
    public void add(String key, Long time, List<User> users){
        Gson gson = new Gson();
        redisTemplate.opsForValue().set(key,gson.toJson(users),time,TimeUnit.MINUTES);
    }

    /**
     * 得到一个对象
     * @param key
     * @return
     */
    public User get(String key){
        Gson gson = new Gson();
        User user = null;
        String userJson = redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(userJson))user = gson.fromJson(userJson, User.class);
        return user;
    }

    /**
     * 得到一个列表的对象
     * @param key
     * @return
     */
    public List<User> users(String key){
        Gson gson = new Gson();
        List<User> users = null;
        String usersJson = redisTemplate.opsForValue().get(key);
        // 将期望解析的数据类型返回给gson
        if(!StringUtils.isEmpty(usersJson))users = gson.fromJson(usersJson,new TypeToken<List<User>>(){}.getType());
        return users;
    }

    /**
     * 删除一个对象
     * @param key
     */
    public void delete(String key){
        redisTemplate.opsForValue().getOperations().delete(key);
    }

}
