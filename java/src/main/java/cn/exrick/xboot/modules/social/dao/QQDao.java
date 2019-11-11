package cn.exrick.xboot.modules.social.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.social.entity.QQ;

/**
 * qq登录数据处理层
 * @author Exrick
 */
public interface QQDao extends XbootBaseDao<QQ,String> {

    /**
     * 通过openId获取
     * @param openId
     * @return
     */
    QQ findByOpenId(String openId);

    /**
     * 通过username获取
     * @param username
     * @return
     */
    QQ findByRelateUsername(String username);

    /**
     * 通过username删除
     * @param username
     */
    void deleteByUsername(String username);
}