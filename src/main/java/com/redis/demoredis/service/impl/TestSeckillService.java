package com.redis.demoredis.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestSeckillService {
    private  int NUM; //抢购数

    TestSeckillService(){
        this.NUM=10;
    }
    public List<Map<String,Object>> getSeckill(List<Map<String,String>> orderList){
        List<Map<String,Object>> rest=new ArrayList<>();
        Iterator it= orderList.iterator();
        //status 1抢到了 0未抢到
        while (it.hasNext()){
            Map<String,Object> map=(Map) it.next();
            map.put("orderTime","orderTime");
            map.put("test","test");
            if(NUM>0){
                map.put("status","1");
            }else {
                map.put("status","0");
            }
            --NUM;
            rest.add(map);
        }
        return rest;
    }

}
