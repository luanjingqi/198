package cn.exrick.xboot.common.lock;

/**
 * 分布式锁模板类
 * @author sunyujia@aliyun.com https://github.com/yujiasun/Distributed-Kit
 */
public interface DistributedLockTemplate {

    /**
     * 执行获取锁方法
     * @param lockId 锁id(对应业务唯一ID)
     * @param timeout 单位毫秒
     * @param callback 回调函数
     * @return
     */
    Object execute(String lockId, Integer timeout, Callback callback);
}
