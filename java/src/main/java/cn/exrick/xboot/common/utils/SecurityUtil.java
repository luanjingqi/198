package cn.exrick.xboot.common.utils;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.constant.SecurityConstant;
import cn.exrick.xboot.common.vo.TokenUser;
import cn.exrick.xboot.config.properties.XbootTokenProperties;
import cn.exrick.xboot.modules.base.entity.*;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.exrick.xboot.modules.base.service.mybatis.IUserRoleService;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Component
public class SecurityUtil {

    @Autowired
    private XbootTokenProperties tokenProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getToken(String username, Boolean saveLogin){

        Boolean saved = false;
        if(saveLogin==null||saveLogin){
            saved = true;
            if(!tokenProperties.getRedis()){
                tokenProperties.setTokenExpireTime(tokenProperties.getSaveLoginTime() * 60 * 24);
            }
        }
        // 生成token
        User u = userService.findByUsername(username);
        List<String> list = new ArrayList<>();
        // 缓存权限
        if(tokenProperties.getStorePerms()){
            for(Permission p : u.getPermissions()){
                if(CommonConstant.PERMISSION_OPERATION.equals(p.getType())
                        && StrUtil.isNotBlank(p.getTitle())
                        && StrUtil.isNotBlank(p.getPath())) {
                    list.add(p.getTitle());
                }
            }
            for(Role r : u.getRoles()){
                list.add(r.getName());
            }
        }
        // 登陆成功生成token
        String token;
        if(tokenProperties.getRedis()){
            // redis
            token = UUID.randomUUID().toString().replace("-", "");
            TokenUser user = new TokenUser(u.getUsername(), list, saved);
            // 单设备登录 之前的token失效
            if(tokenProperties.getSdl()) {
                String oldToken = redisTemplate.opsForValue().get(SecurityConstant.USER_TOKEN + u.getUsername());
                if (StrUtil.isNotBlank(oldToken)) {
                    redisTemplate.delete(SecurityConstant.TOKEN_PRE + oldToken);
                }
            }
            if(saved){
                redisTemplate.opsForValue().set(SecurityConstant.USER_TOKEN + u.getUsername(), token, tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
                redisTemplate.opsForValue().set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(user), tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
            }else{
                redisTemplate.opsForValue().set(SecurityConstant.USER_TOKEN + u.getUsername(), token, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(user), tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
            }
        }else{
            // jwt
            token = SecurityConstant.TOKEN_SPLIT + Jwts.builder()
                    //主题 放入用户名
                    .setSubject(u.getUsername())
                    //自定义属性 放入用户拥有请求权限
                    .claim(SecurityConstant.AUTHORITIES, new Gson().toJson(list))
                    //失效时间
                    .setExpiration(new Date(System.currentTimeMillis() + tokenProperties.getTokenExpireTime() * 60 * 1000))
                    //签名算法和密钥
                    .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                    .compact();
        }
        return token;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public User getCurrUser(){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(user.getUsername());
    }

    /**
     * 获取当前用户数据权限 null代表具有所有权限
     */
    public List<String> getDeparmentIds(){

        List<String> deparmentIds = new ArrayList<>();
        User u = getCurrUser();
        // 用户角色
        List<Role> userRoleList = iUserRoleService.findByUserId(u.getId());
        // 判断有无全部数据的角色
        Boolean flagAll = false;
        for(Role r : userRoleList){
            if(r.getDataType()==null||r.getDataType().equals(CommonConstant.DATA_TYPE_ALL)){
                flagAll = true;
                break;
            }
        }
        if(flagAll){
            return null;
        }
        // 查找自定义
        return iUserRoleService.findDepIdsByUserId(u.getId());
    }

    /**
     * 通过用户名获取用户拥有权限
     * @param username
     */
    public List<GrantedAuthority> getCurrUserPerms(String username){

        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Permission p : userService.findByUsername(username).getPermissions()){
            authorities.add(new SimpleGrantedAuthority(p.getTitle()));
        }
        return authorities;
    }
}
