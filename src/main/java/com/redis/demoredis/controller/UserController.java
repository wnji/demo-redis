package com.redis.demoredis.controller;

import com.redis.demoredis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/**
 * 多线程并发
 * 如果使用线程优化
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 没有优化
     * @param userId
     * @return
     */
    @GetMapping("getUserInfo")
    public Object getUserInfo(String userId){
    return userService.getUserInfo(userId);
    }

    /**
     * 使用Thead优化
     * 问题多个线程会造成堵塞
     * @param userId
     * @return
     */
    @GetMapping("getUserInfoThread")
    public Object getUserInfoThread(String userId){
        return userService.getUserInfoThread(userId);
    }

    /**
     * 使用Thead优化
     * 使用Servlet3.0 异步请求解决线程堵塞线程提升吞吐量
     * 问题线程多会造成 其它服务接口压力大
     * @param userId
     * @return
     */
    @GetMapping("getUserInfoThreadS")
    public Object getUserInfoThreadS(String userId){
        long timeMillis=System.currentTimeMillis();
        Callable<Object> callable=new Callable<Object>() {
            @Override
            public Object call() throws Exception {
             Object object=   userService.getUserInfoThread(userId);
                long ss=System.currentTimeMillis()-timeMillis;
             System.out.println("线程耗时：========"+ss);
                return object;
            }
        };
        long s=System.currentTimeMillis()-timeMillis;
        System.out.println("getUserInfoThreadS耗时：========"+s);

        return callable;
    }
}
