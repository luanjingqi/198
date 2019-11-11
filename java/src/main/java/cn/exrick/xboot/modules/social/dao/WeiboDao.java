package cn.exrick.xboot.modules.social.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.social.entity.Weibo;

/**
 * 微博登录数据处理层
 * @author Exrick
 */
public interface WeiboDao extends XbootBaseDao<Weibo,String> {

    /**
     * 通过openId获取
     * @param openId
     * @return
     */
    Weibo findByOpenId(String openId);

    /**
     * 通过username获取
     * @param username
     * @return
     */
    Weibo findByRelateUsername(String username);

    /**
     * 通过username删除
     * @param username
     */
    void deleteByUsername(String username);
}