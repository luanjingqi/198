package cn.exrick.xboot.modules.social.controller;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.social.entity.Github;
import cn.exrick.xboot.modules.social.entity.QQ;
import cn.exrick.xboot.modules.social.entity.Weibo;
import cn.exrick.xboot.modules.social.service.GithubService;
import cn.exrick.xboot.modules.social.service.QQService;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.exrick.xboot.modules.social.service.WeiboService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Slf4j
@Api(description = "绑定第三方账号接口")
@RequestMapping("/xboot/social")
@RestController
public class RelateController {

    @Autowired
    private UserService userService;

    @Autowired
    private GithubService githubService;

    @Autowired
    private QQService qqService;

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/relate", method = RequestMethod.POST)
    @ApiOperation(value = "绑定账号")
    public Result<Object> relate(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam Integer socialType,
                                 @RequestParam String id){

        User user = userService.findByUsername(username);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("账号不存在");
        }
        if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        String JWT = "";
        // 从redis中获取表id
        String ID = redisTemplate.opsForValue().get(id);
        if(StrUtil.isBlank(ID)){
            return new ResultUtil<Object>().setErrorMsg("无效的id");
        }
        // 绑定github
        if(CommonConstant.SOCIAL_TYPE_GITHUB.equals(socialType)){
            Github g = githubService.findByRelateUsername(username);
            if(g!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有Github账号，请先进行解绑操作");
            }
            Github github = githubService.get(ID);
            if(github==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(github.getIsRelated()&&StrUtil.isNotBlank(github.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该Github账号已绑定有用户，请先进行解绑操作");
            }
            github.setIsRelated(true);
            github.setRelateUsername(username);
            githubService.update(github);
            JWT = securityUtil.getToken(username, true);
        }else if(CommonConstant.SOCIAL_TYPE_QQ.equals(socialType)){
            QQ q = qqService.findByRelateUsername(username);
            if(q!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有QQ账号，请先进行解绑操作");
            }
            QQ qq = qqService.get(ID);
            if(qq==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(qq.getIsRelated()&&StrUtil.isNotBlank(qq.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该QQ账号已绑定有用户，请先进行解绑操作");
            }
            qq.setIsRelated(true);
            qq.setRelateUsername(username);
            qqService.update(qq);
            JWT = securityUtil.getToken(username, true);
        }else if(CommonConstant.SOCIAL_TYPE_WEIBO.equals(socialType)){
            Weibo w = weiboService.findByRelateUsername(username);
            if(w!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有微博账号，请先进行解绑操作");
            }
            Weibo weibo = weiboService.get(ID);
            if(weibo==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(weibo.getIsRelated()&&StrUtil.isNotBlank(weibo.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该微博账号已绑定有用户，请先进行解绑操作");
            }
            weibo.setIsRelated(true);
            weibo.setRelateUsername(username);
            weiboService.update(weibo);
            JWT = securityUtil.getToken(username, true);
        }
        // 存入redis
        String JWTKey = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(JWTKey, JWT, 2L, TimeUnit.MINUTES);
        // 绑定成功删除缓存
        redisTemplate.delete("relate::relatedInfo:" + username);
        return new ResultUtil<Object>().setData(JWTKey);
    }

    @RequestMapping(value = "/getJWT", method = RequestMethod.GET)
    @ApiOperation(value = "获取JWT")
    public Result<Object> getJWT(@RequestParam String JWTKey){

        String JWT = redisTemplate.opsForValue().get(JWTKey);
        if(StrUtil.isBlank(JWT)){
            return new ResultUtil<Object>().setErrorMsg("获取JWT失败");
        }
        return new ResultUtil<Object>().setData(JWT);
    }
}
