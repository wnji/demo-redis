package com.redis.demoredis.service.impl;

import com.redis.demoredis.pojo.User;
import com.redis.demoredis.pojo.UserWallet;
import com.redis.demoredis.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TestUserServer testUserServer;
    //没有优化
    @Override
    public Object getUserInfo(String userId) {
        long s=System.currentTimeMillis();
        //使用200毫秒
        User user=  testUserServer.getUser();
        //使用200毫秒
        UserWallet userWallet=  testUserServer.getUserWallet();
        Map<String,Object> json=new HashMap<>(2);
        json.put("user",user);
        json.put("userWallet",userWallet);
        long ss= System.currentTimeMillis()-s;
        System.out.println("耗时-----------"+ss);
        return json;
    }
    //使用线程优化
    @Override
    public Object getUserInfoThread(String userId) {
        long s=System.currentTimeMillis();
        //线程启动一 完成一个接口
        Callable<User> user=new Callable<User>() {
            @Override
            public User call() throws Exception {
                //使用200毫秒
                User user=  testUserServer.getUser();
                return user;
            }
        };
        //多线程使用FutureTask优化
        FutureTask<User> userFutureTask=new FutureTask<>(user);
            Thread thread=new Thread(userFutureTask);
            thread.start();

        //线程启动二 完成一个接口
        Callable<UserWallet> userWallet=new Callable<UserWallet>() {
            @Override
            public UserWallet call() throws Exception {
                //使用200毫秒
                UserWallet userWallet=  testUserServer.getUserWallet();
                return userWallet;
            }
        };
        FutureTask<UserWallet> userWalletFutureTask=new FutureTask<>(userWallet);
        Thread thread1=new Thread(userWalletFutureTask);
        thread1.start();
        Map<String,Object> json=new HashMap<>(2);

        try {
            json.put("user",userFutureTask.get());
            json.put("userWallet",userWalletFutureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long ss= System.currentTimeMillis()-s;
        System.out.println("耗时Thread-----------"+ss);
        return json;
    }

    public Map<String,Object> queryOrderInfo(String orderCode){
        System.out.println("调用接口");
        return testUserServer.queryOrderIfo(orderCode);
    }
    //封装请求
    class Request{
        String serialNo;//内部生成
        String orderCode;//请求参数可能会重复
        CompletableFuture<Map<String,Object>> future;
    }
    //队列  （阻塞队列：安全线程，性能考虑）
    LinkedBlockingDeque<Request> linkedBlockingDeque=new LinkedBlockingDeque<>();
    //后端通过批量的方式处理
    public Map<String,Object> queryOrderBatch(String orderCode) throws Exception {
        String serialNo= UUID.randomUUID().toString();
        //CompletableFuture 监听结果（线程）
        CompletableFuture<Map<String,Object>> future=new CompletableFuture<>();
        //绑定业务线程serialNo-orderCode
        Request request=new Request();
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
                List<Map<String,String>>mapList=new ArrayList<>();//批量调用的时候请求参数
                List<Request> requestList=new ArrayList<>();//
                for (int i=0;i<size;i++){
                    Request request=linkedBlockingDeque.poll();
                    Map<String,String> map=new HashMap<>();
                    map.put("serialNo",request.serialNo);
                    map.put("orderCode",request.orderCode);
                    mapList.add(map);
                    requestList.add(request);
                }
                System.out.println("批量请求的数据量----"+size);
                //调用批量接口
                List<Map<String,Object>> responses=testUserServer.queryOrderBatch(mapList);
                for(Request request:requestList){
                    String serialNo=request.serialNo;
                    for (Map<String,Object> map:responses){
                        if(serialNo.equals(map.get("serialNo").toString())){
                            request.future.complete(map);
                            break;
                        }
                    }
                }


            }
        },100,10,TimeUnit.MILLISECONDS);
    }
}
