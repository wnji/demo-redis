package com.redis.demoredis;

import com.redis.demoredis.service.impl.SeckillService;
import com.redis.demoredis.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.concurrent.CountDownLatch;


@SpringBootTest()
class DemoRedisApplicationTests {
//    @Autowired
//    UserServiceImpl userService;
    @Autowired
    SeckillService seckillService;
    private static final int THREAD_NUM=100;
    private CountDownLatch countDownLatch=new CountDownLatch(THREAD_NUM);

//    @Test
//    void contextLoads() throws InterruptedException {
//        for (int i=0;i<THREAD_NUM;i++){
//            final String orderCode=String.valueOf(100+i);
//            Thread thread=new Thread(()->{
//                try {
//                    countDownLatch.countDown();
//                    countDownLatch.await();
//                    userService.queryOrderBatch(orderCode);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            );
//            thread.start();
//        }
//        Thread.sleep(5000);
//
//    }
    @Test
    void contextLoadss() throws InterruptedException {
        //模拟100个人抢购
        for (int i=0;i<THREAD_NUM;i++){
            final String orderCode=String.valueOf(100+i);
            Thread thread=new Thread(()->{
                try {
                    countDownLatch.countDown();
                    countDownLatch.await();
                    Map<String, Object> s=seckillService.queryOrderBatch(orderCode);
                    System.out.println(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            );
            thread.start();
        }
        Thread.sleep(5000);

    }


}
