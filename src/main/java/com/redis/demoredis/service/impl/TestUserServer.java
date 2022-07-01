package com.redis.demoredis.service.impl;

import com.redis.demoredis.pojo.User;
import com.redis.demoredis.pojo.UserWallet;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestUserServer {


    public User getUser(){
        try {
            //模拟请求使用时间
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user= new User();
        user.setUserName("user");
        user.setEmail("email");
        user.setUserId(1);
        return user;
    }
    public UserWallet getUserWallet(){
        try {
            //模拟请求使用时间
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserWallet userWallet=new UserWallet();
        userWallet.setMoney(1000);
        userWallet.setUnit("元");
        return userWallet;
    }

    public Map<String,Object>  queryOrderIfo(String orderCode){
        Map<String,Object> rest=new HashMap<>();
        return rest;
    }
    public List<Map<String,Object>> queryOrderBatch(List<Map<String,String>> orderList){
        List<Map<String,Object>> rest=new ArrayList<>();
       Iterator it= orderList.iterator();
       while (it.hasNext()){
           Map<String,Object> map=(Map) it.next();
           map.put("orderTime","orderTime");
           map.put("test","test");
            rest.add(map);
       }
       return rest;
    }
}
