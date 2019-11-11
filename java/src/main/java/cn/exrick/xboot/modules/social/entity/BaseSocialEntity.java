package cn.exrick.xboot.modules.social.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.MappedSuperclass;

/**
 * @author Exrick
 */
@Data
@MappedSuperclass
public class BaseSocialEntity extends XbootBaseEntity {

    @ApiModelProperty(value = "唯一id")
    private String openId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "是否绑定账号 默认false")
    private Boolean isRelated = false;

    @ApiModelProperty(value = "绑定用户账号")
    private String relateUsername;
}
