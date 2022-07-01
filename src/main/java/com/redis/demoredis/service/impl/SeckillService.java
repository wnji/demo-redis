package com.redis.demoredis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Service
public class SeckillService {
    @Autowired
    private TestSeckillService testSeckillService;
    //封装请求
    class Request{
        String serialNo;//内部生成
        String orderCode;//请求参数可能会重复
        CompletableFuture<Map<String,Object>> future;
    }
    //队列  （阻塞队列：安全线程，性能考虑）
    LinkedBlockingDeque<SeckillService.Request> linkedBlockingDeque=new LinkedBlockingDeque<>();
    //后端通过批量的方式处理
    public Map<String,Object> queryOrderBatch(String orderCode) throws Exception {
        String serialNo= UUID.randomUUID().toString();
        //CompletableFuture 监听结果（线程）
        CompletableFuture<Map<String,Object>> future=new CompletableFuture<>();
        //绑定业务线程serialNo-orderCode
        SeckillService.Request request=new SeckillService.Request();
        request.serialNo=serialNo;
        request.orderCode=orderCode;
        request.future=future;
        linkedBlockingDeque.add(request);//把数据放入队列中 接口完成
        return future.get();//没有拿到结果，阻塞，如果拿到结果 类似FutureTask.get()
    }


    //定时任务
    @PostConstruct
    public void doBusiness(){
        //定时的线程池，每隔10毫秒运行一次
        ScheduledExecutorService service= Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int size=linkedBlockingDeque.size();
                if(size==0){
                    return;
                }
                //根据接口封装批量参数
                List<Map<String,String>> mapList=new ArrayList<>();//批量调用的时候请求参数
                List<SeckillService.Request> requestList=new ArrayList<>();//
                for (int i=0;i<size;i++){
                    SeckillService.Request request=linkedBlockingDeque.poll();
                    Map<String,String> map=new HashMap<>();
                    map.put("serialNo",request.serialNo);
                    map.put("orderCode",request.orderCode);
                    mapList.add(map);
                    requestList.add(request);
                }
                System.out.println("批量请求的数据量----"+size);
                //调用批量接口
                List<Map<String,Object>> responses=testSeckillService.getSeckill(mapList);
                for(SeckillService.Request request:requestList){
                    String serialNo=request.serialNo;
                    for (Map<String,Object> map:responses){
                        if(serialNo.equals(map.get("serialNo").toString())){
                            request.future.complete(map);
                            break;
                        }
                    }
                }


            }
        },100,10, TimeUnit.MILLISECONDS);
    }
}
