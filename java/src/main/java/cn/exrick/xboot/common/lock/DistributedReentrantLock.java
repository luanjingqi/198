package cn.exrick.xboot.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式可重入锁
 * @author sunyujia@aliyun.com https://github.com/yujiasun/Distributed-Kit
 */
public interface DistributedReentrantLock {

    /**
     * 申请锁 等待限时
     * true获取锁成功 false锁已被占用
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 释放锁
     */
    void unlock();
}
