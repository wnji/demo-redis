package com.redis.demoredis.service;

public interface UserService {
    /**
     * 没有使用线程优化
     * @param userId
     * @return
     */
    public Object getUserInfo(String userId);

    /**
     *使用线程优化
     * @param userId
     * @return
     */
    public Object getUserInfoThread(String userId);
}
