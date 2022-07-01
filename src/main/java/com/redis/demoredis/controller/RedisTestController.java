package com.redis.demoredis.controller;

import com.redis.demoredis.dao.UserDao;
import com.redis.demoredis.util.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RedisTestController {

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    /**
     * Add data2214697432
     * @param key
     * @param val
     * @return
     */
    @RequestMapping("set")
    @ResponseBody
    public String set(String key,String val){
        redisUtil.set(key,val);
        return "key:"+key+"  val:"+val ;
    }

    /**
     * get data
     * @param key
     * @return
     */
    @RequestMapping("get")
    @ResponseBody
    public Object get(String key){
       return redisUtil.get(key);
    }


    @ResponseBody
    @GetMapping("addUser")
    public boolean addUser(@RequestParam("name") String name,@RequestParam("pid") String pid){

        return userDao.addUser(name,pid);
    }
    @ResponseBody
    @GetMapping("getUserList")
    public List<Map<String,String>> getUserList(){
        return userDao.getUserList();
    }

    @ResponseBody
    @GetMapping("getUserCount")
    public int getUserCount(){
        return userDao.getUserCount();
    }


//    @Autowired
//    RabbitTemplate rabbitTemplate;
//    @RequestMapping("send.do")
//    @ResponseBody
//    public String send() {
//        Map<String,String> map=new HashMap<>();
//        map.put("werq","aerw");
//        map.put("wewqadrq","aersa胜多负少w");
//        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
//        return "ok";
//    }
}
